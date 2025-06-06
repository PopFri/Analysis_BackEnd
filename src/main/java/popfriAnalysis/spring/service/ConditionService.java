package popfriAnalysis.spring.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.apiPayload.code.status.ErrorStatus;
import popfriAnalysis.spring.apiPayload.exception.handler.ConditionHandler;
import popfriAnalysis.spring.domain.AnalysisColumn;
import popfriAnalysis.spring.domain.AnalysisCondition;
import popfriAnalysis.spring.domain.AnalysisProcess;
import popfriAnalysis.spring.domain.Calculator;
import popfriAnalysis.spring.repository.CalculatorRepository;
import popfriAnalysis.spring.repository.ColumnRepository;
import popfriAnalysis.spring.repository.ConditionRepository;
import popfriAnalysis.spring.web.dto.ConditionRequest;
import popfriAnalysis.spring.web.dto.ConditionResponse;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ConditionService {
    private final ConditionRepository conditionRepository;
    private final ColumnRepository columnRepository;
    private final CalculatorRepository calculatorRepository;

    private int postfixIndex = 0;

    @Transactional
    public void delCondition(AnalysisProcess process){
        List<AnalysisColumn> columnList = columnRepository.findByProcess(process);

        calculatorRepository.deleteAll(calculatorRepository.findByProcess(process));
        columnList.forEach(column -> conditionRepository.deleteAll(conditionRepository.findByColumn(column)));
    }

    public Boolean saveConditions(AnalysisProcess process, ConditionRequest.AddAnalysisConditionDto dto) {
        List<ConditionRequest.ConditionDto> conditionList = dto.getCondition();

        for (ConditionRequest.ConditionDto conditionDto : conditionList) {
            savePostfix(process, conditionDto);
        }

        // 루트 조건들끼리 OR (Postfix: 피연산자 뒤에)
        for (int i = 0; i < conditionList.size() - 1; i++) {
            calculatorRepository.save(
                    Calculator.builder()
                            .process(process)
                            .condition(null)
                            .relation("OR")
                            .calIndex(postfixIndex++)
                            .build()
            );
        }

        return true;
    }

    private void savePostfix(AnalysisProcess process, ConditionRequest.ConditionDto dto) {
        boolean hasChildren = dto.getCondition() != null && !dto.getCondition().isEmpty();

        if (hasChildren) {
            List<ConditionRequest.ConditionDto> childList = dto.getCondition();

            // 1. 자식 조건들 먼저 후위 저장
            for (ConditionRequest.ConditionDto child : childList) {
                savePostfix(process, child);
            }

            // 2. 자식 간 OR 연산 (Postfix이므로 마지막에 몰아서)
            for (int i = 0; i < childList.size() - 1; i++) {
                calculatorRepository.save(
                        Calculator.builder()
                                .process(process)
                                .condition(null)
                                .relation("OR")
                                .calIndex(postfixIndex++)
                                .build()
                );
            }
        }

        // 3. 현재 피연산자 저장
        AnalysisColumn column = columnRepository.findByProcessAndName(process, dto.getColumn())
                .orElseThrow(() -> new ConditionHandler(ErrorStatus._NOT_EXIST_COLUMN));

        AnalysisCondition condition = conditionRepository.save(
                AnalysisCondition.builder()
                        .column(column)
                        .operator(dto.getOp())
                        .valueC(dto.getValue())
                        .build()
        );

        calculatorRepository.save(
                Calculator.builder()
                        .process(process)
                        .condition(condition)
                        .relation(null)
                        .calIndex(postfixIndex++)
                        .build()
        );

        // 4. 자식이 있었으면 자식들과 부모 사이 AND
        if (hasChildren) {
            calculatorRepository.save(
                    Calculator.builder()
                            .process(process)
                            .condition(null)
                            .relation("AND")
                            .calIndex(postfixIndex++)
                            .build()
            );
        }
    }

    public List<ConditionResponse.getConditionListResDTO> getCondition(AnalysisProcess process){
        return calculatorRepository.findByProcess(process).stream()
                .sorted(Comparator.comparing(Calculator::getCalIndex))
                .map(calculator -> {
                    AnalysisCondition condition = calculator.getCondition();
                    if(condition == null)
                        return null;

                    return ConditionResponse.getConditionListResDTO.builder()
                            .conditionId(condition.getConditionId())
                            .condition(
                                    condition.getColumn().getName() + " " + condition.getOperator() + " " + condition.getValueC())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public List<Calculator> changePostfix(List<Calculator> calculatorList){

        return null;
    }
}
