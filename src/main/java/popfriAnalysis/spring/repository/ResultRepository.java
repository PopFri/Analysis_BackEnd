package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.AnalysisResult;

public interface ResultRepository extends JpaRepository<AnalysisResult, Long> {
}
