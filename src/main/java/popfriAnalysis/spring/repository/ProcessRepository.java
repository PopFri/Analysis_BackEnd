package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import popfriAnalysis.spring.domain.AnalysisProcess;

import java.util.List;
import java.util.Optional;

public interface ProcessRepository extends JpaRepository<AnalysisProcess, Long> {
    Optional<AnalysisProcess> findByProcessId(Long processId);

    @Query("SELECT DISTINCT p FROM AnalysisProcess p JOIN FETCH p.columnList")
    List<AnalysisProcess> findAllWithColumnList();

    @Query("SELECT DISTINCT p FROM AnalysisProcess p JOIN FETCH p.calculatorList")
    List<AnalysisProcess> findAllWithCalculatorList();
}
