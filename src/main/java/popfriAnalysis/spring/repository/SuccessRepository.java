package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import popfriAnalysis.spring.domain.AnalysisSuccess;

import java.time.LocalDateTime;

public interface SuccessRepository extends JpaRepository<AnalysisSuccess, Long> {
}
