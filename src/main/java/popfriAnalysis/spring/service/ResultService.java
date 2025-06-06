package popfriAnalysis.spring.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.domain.*;
import popfriAnalysis.spring.repository.CalculatorRepository;
import popfriAnalysis.spring.repository.FailRepository;
import popfriAnalysis.spring.repository.ProcessRepository;
import popfriAnalysis.spring.repository.SuccessRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultService {
    private final ProcessRepository processRepository;
    private final CalculatorRepository calculatorRepository;
    private final SuccessRepository successRepository;
    private final FailRepository failRepository;

    @KafkaListener(topics = "matomo-log", groupId = "consumer_group01")
    @Transactional
    public void saveResult(String message) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(message);
        log.info("Success String -> JSON");

        String logId = jsonObject.get("QUERY_pv_id").toString();
        log.info("Success to get QUERY_pv_id: " + logId);

        processRepository.findAll().stream().parallel().forEach(process -> {
            if(evaluateProcessLogic(jsonObject, process)){
                process.getColumnList().forEach(column ->
                        successRepository.save(AnalysisSuccess.builder()
                                .column(column)
                                .valueR(jsonObject.get(column.getName()).toString())
                                .logId(logId).build())
                );
                log.info("Save Result Success(success_result) logId: " + logId);
            } else {
                process.getColumnList().forEach(column ->
                        failRepository.save(AnalysisFail.builder()
                                .column(column)
                                .valueR(jsonObject.get(column.getName()).toString())
                                .logId(logId).build())
                );
                log.info("Save Result Success(fail_result) logId: " + logId);
            }
        });
    }

    public boolean evaluateProcessLogic(JSONObject jsonObject, AnalysisProcess process) {
        List<Calculator> entries = calculatorRepository.findByProcessOrderByCalIndex(process);

        Deque<Boolean> stack = new ArrayDeque<>();

        for (Calculator entry : entries) {
            if (entry.getCondition() != null) {
                // 피연산자: 조건 평가
                boolean result = calculateColumn(jsonObject, entry.getCondition());
                log.info("Start to calculate Column (column name: " + entry.getCondition().getColumn().getName() + ")");
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
                    default -> throw new IllegalArgumentException("Unknown relation: " + entry.getRelation());
                }
            }
        }

        if (stack.size() != 1) {
            throw new IllegalStateException("Invalid postfix expression for process " + process.getProcessId());
        }

        return stack.pop();
    }

    private Boolean calculateColumn(JSONObject jsonObject, AnalysisCondition condition){
        String operator = condition.getOperator();
        String value = condition.getValueC();

        log.info("Start to find column by JSON (column name: " + condition.getColumn().getName() + ")");
        Object jsonValue = jsonObject.get(condition.getColumn().getName());
        if(jsonValue == null){
            log.error("Can not find column: " + condition.getColumn().getName());
            return false;
        }

        String actualValue = jsonValue.toString();

        return switch (operator) {
            case "==" -> actualValue.equals(value);
            case "!=" -> !actualValue.equals(value);
            case ">" -> Double.parseDouble(actualValue) > Double.parseDouble(value);
            case "<" -> Double.parseDouble(actualValue) < Double.parseDouble(value);
            case ">=" -> Double.parseDouble(actualValue) >= Double.parseDouble(value);
            case "<=" -> Double.parseDouble(actualValue) <= Double.parseDouble(value);
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }
}
