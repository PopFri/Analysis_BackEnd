package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.AnalysisFail;

public interface FailRepository extends JpaRepository<AnalysisFail, Long> {
}
