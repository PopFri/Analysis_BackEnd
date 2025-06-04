package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.AnalysisSuccess;

public interface SuccessRepository extends JpaRepository<AnalysisSuccess, Long> {
}
