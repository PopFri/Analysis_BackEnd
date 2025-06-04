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

    public Boolean saveConditions(AnalysisProcess process ,ConditionRequest.AddAnalysisConditionDto dto) {
        for (int i = 0; i < dto.getCondition().size(); i++) {
            savePostfixRecursive(process, dto.getCondition().get(i));

            // 같은 깊이 일시 OR(루트 개별 처리)
            if (i < dto.getCondition().size() - 1) {
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

        return true;
    }

    private void savePostfixRecursive(AnalysisProcess process, ConditionRequest.ConditionDto conditionDto) {
        AnalysisColumn column = columnRepository.findByProcessAndName(process, conditionDto.getColumn())
                .orElseThrow(() -> new ConditionHandler(ErrorStatus._NOT_EXIST_COLUMN));

        AnalysisCondition condition = conditionRepository.save(
                AnalysisCondition.builder()
                        .column(column)
                        .operator(conditionDto.getOp())
                        .valueC(conditionDto.getValue())
                        .build());

        // 하위 조건이 존재할 시 실행
        if (conditionDto.getCondition() != null && !conditionDto.getCondition().isEmpty()) {
            for (int i = 0; i < conditionDto.getCondition().size(); i++) {
                savePostfixRecursive(process, conditionDto.getCondition().get(i));

                // 같은 깊이일 시 OR
                if (i < conditionDto.getCondition().size() - 1) {
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
            // 이전 연산과 AND
            calculatorRepository.save(
                    Calculator.builder()
                            .process(process)
                            .condition(null)
                            .relation("AND")
                            .calIndex(postfixIndex++)
                            .build()
            );
        }

        // 현재 피연산자 저장
        calculatorRepository.save(
                Calculator.builder()
                        .process(process)
                        .condition(condition)
                        .relation(null)
                        .calIndex(postfixIndex++)
                        .build()
        );
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
}
