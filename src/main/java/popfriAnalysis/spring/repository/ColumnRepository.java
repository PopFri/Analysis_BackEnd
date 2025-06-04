package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.AnalysisColumn;
import popfriAnalysis.spring.domain.AnalysisProcess;

import java.util.List;
import java.util.Optional;

public interface ColumnRepository extends JpaRepository<AnalysisColumn, Long> {
    List<AnalysisColumn> findByProcess(AnalysisProcess process);

    Optional<AnalysisColumn> findByProcessAndName(AnalysisProcess process, String name);
}
