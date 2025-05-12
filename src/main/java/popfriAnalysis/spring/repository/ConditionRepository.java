package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.AnalysisCondition;

public interface ConditionRepository extends JpaRepository<AnalysisCondition, Long> {
}
