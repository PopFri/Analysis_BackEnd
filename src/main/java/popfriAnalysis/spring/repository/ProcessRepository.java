package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.AnalysisProcess;

public interface ProcessRepository extends JpaRepository<AnalysisProcess, Long> {
}
