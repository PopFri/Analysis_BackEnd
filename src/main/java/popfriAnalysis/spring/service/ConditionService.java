package popfriAnalysis.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.converter.ConditionConverter;
import popfriAnalysis.spring.domain.AnalysisCondition;
import popfriAnalysis.spring.domain.AnalysisProcess;
import popfriAnalysis.spring.repository.ConditionRepository;
import popfriAnalysis.spring.web.dto.ConditionRequest;
import popfriAnalysis.spring.web.dto.ConditionResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConditionService {
    private final ConditionRepository conditionRepository;

    public Boolean addAnalysisCondition(AnalysisProcess process, List<ConditionRequest.ConditionDto> conditionDtoList){
        List<AnalysisCondition> conditionList = ConditionConverter.conditionDtoToConditionValue(conditionDtoList).stream()
                .map(operator -> AnalysisCondition.builder()
                        .process(process)
                        .operator(operator)
                        .build())
                .toList();

        conditionRepository.saveAll(conditionList);

        return true;
    }

    public void delAnalysisConditionAll(AnalysisProcess process){
        List<AnalysisCondition> oldConditionList = conditionRepository.findByProcess(process);

        conditionRepository.deleteAll(oldConditionList);
    }

    public List<ConditionResponse.getConditionListResDTO> getConditionListToProcess(AnalysisProcess process){
        return conditionRepository.findByProcess(process).stream()
                .map(condition -> ConditionResponse.getConditionListResDTO.builder()
                        .conditionId(condition.getConditionId())
                        .operator(condition.getOperator())
                        .build())
                .toList();
    }
}
