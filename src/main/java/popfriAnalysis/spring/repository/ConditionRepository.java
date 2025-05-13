package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.AnalysisCondition;
import popfriAnalysis.spring.domain.AnalysisProcess;

import java.util.List;

public interface ConditionRepository extends JpaRepository<AnalysisCondition, Long> {
    List<AnalysisCondition> findByProcess(AnalysisProcess process);
}
