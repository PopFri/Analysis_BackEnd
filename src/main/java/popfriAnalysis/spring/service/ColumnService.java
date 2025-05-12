package popfriAnalysis.spring.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.domain.AnalysisColumn;
import popfriAnalysis.spring.domain.AnalysisProcess;
import popfriAnalysis.spring.repository.ColumnRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColumnService {
    private final ColumnRepository columnRepository;

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
        List<AnalysisColumn> oldColumnList = columnRepository.findByProcess(process);

        columnRepository.deleteAll(oldColumnList);
    }
}
