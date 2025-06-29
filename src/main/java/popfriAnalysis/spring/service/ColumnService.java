package popfriAnalysis.spring.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.apiPayload.code.status.ErrorStatus;
import popfriAnalysis.spring.apiPayload.exception.handler.ColumnHandler;
import popfriAnalysis.spring.domain.AnalysisColumn;
import popfriAnalysis.spring.domain.AnalysisProcess;
import popfriAnalysis.spring.repository.CalculatorRepository;
import popfriAnalysis.spring.repository.ColumnRepository;
import popfriAnalysis.spring.web.dto.ColumnResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColumnService {
    private final ColumnRepository columnRepository;
    private final CalculatorRepository calculatorRepository;

    public Boolean addAnalysisColumn(AnalysisProcess process, List<String> input){
        List<AnalysisColumn> columnList = input.stream()
                .map(name -> AnalysisColumn.builder()
                        .process(process)
                        .name(name)
                        .build())
                .toList();

        columnRepository.saveAll(columnList);

        return true;
    }

    @Transactional
    public void delAnalysisColumnAll(AnalysisProcess process){
        calculatorRepository.deleteAll(calculatorRepository.findByProcess(process));

        List<AnalysisColumn> oldColumnList = columnRepository.findByProcess(process);

        columnRepository.deleteAll(oldColumnList);
    }

    public List<ColumnResponse.getColumnListResDTO> getColumnList(AnalysisProcess process){
        return columnRepository.findByProcess(process).stream()
                .map(analysisColumn -> ColumnResponse.getColumnListResDTO.builder()
                        .columnId(analysisColumn.getColumnId())
                        .columnName(analysisColumn.getName())
                        .build())
                .toList();
    }

    public AnalysisColumn getColumnToName(AnalysisProcess process, String name){
        return columnRepository.findByProcessAndName(process, name).orElseThrow(() -> new ColumnHandler(ErrorStatus._NOT_EXIST_COLUMN));
    }
}
