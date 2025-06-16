package popfriAnalysis.spring.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.apiPayload.code.status.ErrorStatus;
import popfriAnalysis.spring.apiPayload.exception.handler.ResultHandler;
import popfriAnalysis.spring.domain.*;
import popfriAnalysis.spring.domain.common.BaseEntity;
import popfriAnalysis.spring.repository.CalculatorRepository;
import popfriAnalysis.spring.repository.FailRepository;
import popfriAnalysis.spring.repository.ProcessRepository;
import popfriAnalysis.spring.repository.SuccessRepository;
import popfriAnalysis.spring.web.dto.ResultResponse;
import popfriAnalysis.spring.repository.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultService {
    private final ProcessRepository processRepository;
    private final CalculatorRepository calculatorRepository;
    private final SuccessRepository successRepository;
    private final FailRepository failRepository;
    private final LogDataRepository logDataRepository;

    @KafkaListener(topics = "matomo-log", groupId = "analysis_server_consumer_01")
    @Transactional
    public void saveResult(String message) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(message);

        String logId = jsonObject.get("QUERY_pv_id").toString();

        LogData logData = logDataRepository.save(LogData.builder().logId(logId).data(message).build());

        processRepository.findAll().forEach(process -> {
            if(evaluateProcessLogic(jsonObject, process)){
                process.getColumnList().forEach(column -> {
                    Object jsonValue = jsonObject.get(column.getName());
                    if(jsonValue == null){
                        jsonValue = "null";
                    }

                    successRepository.save(AnalysisSuccess.builder()
                            .column(column)
                            .valueR(jsonValue.toString())
                            .logData(logData).build());
                });
                log.info("Save Result Successful(success_result) logId: " + logId + ", processId: " + process.getProcessId());
            } else {
                process.getColumnList().forEach(column -> {
                    Object jsonValue = jsonObject.get(column.getName());;
                    if(jsonValue == null){
                        jsonValue = "null";
                    }

                    failRepository.save(AnalysisFail.builder()
                            .column(column)
                            .valueR(jsonValue.toString())
                            .logData(logData).build());
                });
                log.info("Save Result Successful(fail_result) logId: " + logId + ", processId: " + process.getProcessId());
            }
        });
    }

    @Transactional
    public boolean evaluateProcessLogic(JSONObject jsonObject, AnalysisProcess process) {
        List<Calculator> entries = calculatorRepository.findByProcessOrderByCalIndex(process);

        if(entries.isEmpty()){
            log.info("Condition is empty: processId: " + process.getProcessId());
            return true;
        }

        Deque<Boolean> stack = new ArrayDeque<>();

        for (Calculator entry : entries) {
            if (entry.getCondition() != null) {
                AnalysisCondition condition = entry.getCondition();
                // 피연산자: 조건 평가
                boolean result = calculateColumn(jsonObject, entry.getCondition());
                if(result) {
                    condition.setSuccessCount(condition.getSuccessCount() + 1);
                }else {
                    condition.setFailCount(condition.getFailCount() + 1);
                }
                stack.push(result);
            } else if (entry.getRelation() != null) {
                switch (entry.getRelation()) {
                    case "AND" -> {
                        boolean right = stack.pop();
                        boolean left = stack.pop();
                        stack.push(left && right);
                    }
                    case "OR" -> {
                        boolean right = stack.pop();
                        boolean left = stack.pop();
                        stack.push(left || right);
                    }
                    default -> throw new ResultHandler(ErrorStatus._NOT_EXIST_RELATION);
                }
            }
        }

        if (stack.size() != 1) {
            throw new ResultHandler(ErrorStatus._CALCULATE_FAIL);
        }

        return stack.pop();
    }

    @Transactional
    public Boolean calculateColumn(JSONObject jsonObject, AnalysisCondition condition) {
        String operator = condition.getOperator();
        String value = condition.getValueC();

        Object jsonValue = jsonObject.get(condition.getColumn().getName());
        if (jsonValue == null) {
            log.error("Can not find column: " + condition.getColumn().getName());
            return false;
        }

        String actualValue = jsonValue.toString();

        boolean result;

        try {
            switch (operator) {
                case "==" -> result = actualValue.equals(value);
                case "!=" -> result = !actualValue.equals(value);
                case ">" -> result = Double.parseDouble(actualValue) > Double.parseDouble(value);
                case "<" -> result = Double.parseDouble(actualValue) < Double.parseDouble(value);
                case ">=" -> result = Double.parseDouble(actualValue) >= Double.parseDouble(value);
                case "<=" -> result = Double.parseDouble(actualValue) <= Double.parseDouble(value);
                default -> throw new ResultHandler(ErrorStatus._NOT_EXIST_OPERATION);
            }
        } catch (NumberFormatException err){
            result = false;
            log.error("Can Solve the Operator (column name: " + actualValue + ")");
        }

        return result;
    }

    public List<ResultResponse.getResultColumn> getResultList(AnalysisProcess process){
        Map<String, List<AnalysisSuccess>> successMap = process.getColumnList().stream()
                .flatMap(column -> column.getSuccessList().stream())
                .collect(Collectors.groupingBy(analysisSuccess -> analysisSuccess.getLogData().getLogId()));

        List<ResultResponse.getResultColumn> resultList = new ArrayList<>();
        for(String logId : successMap.keySet()){
            resultList.add(ResultResponse.getResultColumn.builder()
                    .logId(logId)
                    .columnList(successMap.get(logId)
                            .stream()
                            .map(result -> ResultResponse.getResultColumn.columnDto.builder()
                                    .name(result.getColumn().getName())
                                    .value(result.getValueR())
                                    .build())
                            .toList())
                    .build());
        }

        return resultList;
    }

    public ResultResponse.successDataCountDto successDataCountByCondition(Long processId) {
        List<Calculator> calculatorList = processRepository.findById(processId).orElseThrow().getCalculatorList();
        List<ResultResponse.successDataCountDto.conditionDto> successDataCountDtoList = new ArrayList<>();
        Integer totalCount = 0;
        Integer conditionCount = 0;
        for (Calculator calculator : calculatorList) {
            AnalysisCondition condition = calculator.getCondition();
            if (condition == null || condition.getColumn() == null) continue;
            String columnName = condition.getColumn().getName();
            String operator = condition.getOperator();
            String value = condition.getValueC();
            String strCondition = columnName + " " + operator + " " + value;

            Integer successCount = condition.getSuccessCount() != null ? condition.getSuccessCount() : 0;
            Integer failCount = condition.getFailCount() != null ? condition.getFailCount() : 0;

            successDataCountDtoList.add(ResultResponse.successDataCountDto.conditionDto.builder()
                    .condition(strCondition)
                    .successCount(successCount)
                    .failedCount(failCount)
                    .build());

            totalCount += successCount + failCount;
            conditionCount++;
        }
        return ResultResponse.successDataCountDto.builder()
                .totalCount(totalCount / conditionCount)
                .conditionList(successDataCountDtoList)
                .build();
    }

    public List<ResultResponse.getResultColumn> sortResultList(List<ResultResponse.getResultColumn> dtoList, String columnName, String order){
        if(!(order.equals("asc") || order.equals("ASC") ||order.equals("desc") || order.equals("DESC"))){
            throw new ResultHandler(ErrorStatus._NOT_EXIST_SORT);
        }

        dtoList.sort(Comparator.comparing(dto -> {
            Optional<ResultResponse.getResultColumn.columnDto> sortColumnDto = dto.getColumnList().stream()
                    .filter(columnDto -> columnDto.getName().equals(columnName))
                    .findFirst();

            return sortColumnDto.map(ResultResponse.getResultColumn.columnDto::getValue).orElse(null);
        }));

        if(order.equals("desc") || order.equals("DESC")){
            Collections.reverse(dtoList);
        }

        return dtoList;
    }

    public ResultResponse.SuccessOrFailResponseDto successDataByProcess(Long processId, int page, int size) {
        List<AnalysisColumn> columnList = processRepository.findById(processId)
                .orElseThrow(() -> new IllegalArgumentException("없는 프로세스 ID")).getColumnList();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AnalysisSuccess> successPage = successRepository
                .findByColumnIn(columnList, pageable); // AnalysisSuccess 기준 페이징

        // groupBy logData
        Map<LogData, List<AnalysisSuccess>> groupedMap = successPage.getContent().stream()
                .collect(Collectors.groupingBy(AnalysisSuccess::getLogData));

        List<ResultResponse.successOrFailDataDto> groupedList = groupedMap.entrySet().stream()
                .map(entry -> {
                    LogData log = entry.getKey();
                    List<AnalysisSuccess> filteredSuccessList = entry.getValue();

                    List<ResultResponse.successOrFailDataDto.resultDataDto> dataList = filteredSuccessList.stream()
                            .filter(s -> columnList.contains(s.getColumn()))
                            .map(s -> ResultResponse.successOrFailDataDto.resultDataDto.builder()
                                    .column(s.getColumn().getName())
                                    .value(s.getValueR())
                                    .build())
                            .collect(Collectors.toList());

                    LocalDateTime createdAt = filteredSuccessList.stream()
                            .map(BaseEntity::getCreatedAt)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);

                    return ResultResponse.successOrFailDataDto.builder()
                            .logId(log.getLogId())
                            .createdAt(createdAt)
                            .dataList(dataList)
                            .build();
                })
                .collect(Collectors.toList());

        return ResultResponse.SuccessOrFailResponseDto.builder()
                .totalCount(successPage.getTotalElements())
                .data(groupedList)
                .build();
    }

    public ResultResponse.SuccessOrFailResponseDto failDataByProcess(Long processId, int page, int size) {
        List<AnalysisColumn> columnList = processRepository.findById(processId)
                .orElseThrow(() -> new IllegalArgumentException("없는 프로세스 ID")).getColumnList();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AnalysisFail> failPage = failRepository
                .findByColumnIn(columnList, pageable); // AnalysisSuccess 기준 페이징

        // groupBy logData
        Map<LogData, List<AnalysisFail>> groupedMap = failPage.getContent().stream()
                .collect(Collectors.groupingBy(AnalysisFail::getLogData));

        List<ResultResponse.successOrFailDataDto> groupedList = groupedMap.entrySet().stream()
                .map(entry -> {
                    LogData log = entry.getKey();
                    List<AnalysisFail> filteredFailList = entry.getValue();

                    List<ResultResponse.successOrFailDataDto.resultDataDto> dataList = filteredFailList.stream()
                            .filter(s -> columnList.contains(s.getColumn()))
                            .map(s -> ResultResponse.successOrFailDataDto.resultDataDto.builder()
                                    .column(s.getColumn().getName())
                                    .value(s.getValueR())
                                    .build())
                            .collect(Collectors.toList());

                    LocalDateTime createdAt = filteredFailList.stream()
                            .map(BaseEntity::getCreatedAt)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);

                    return ResultResponse.successOrFailDataDto.builder()
                            .logId(log.getLogId())
                            .createdAt(createdAt)
                            .dataList(dataList)
                            .build();
                })
                .collect(Collectors.toList());

        return ResultResponse.SuccessOrFailResponseDto.builder()
                .totalCount(failPage.getTotalElements())
                .data(groupedList)
                .build();
    }
}
