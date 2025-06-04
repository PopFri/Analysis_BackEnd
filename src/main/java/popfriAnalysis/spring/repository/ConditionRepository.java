package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.AnalysisColumn;
import popfriAnalysis.spring.domain.AnalysisCondition;

import java.util.List;

public interface ConditionRepository extends JpaRepository<AnalysisCondition, Long> {
    List<AnalysisCondition> findByColumn(AnalysisColumn column);
}
