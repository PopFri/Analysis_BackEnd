package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.AnalysisProcess;

import java.util.Optional;

public interface ProcessRepository extends JpaRepository<AnalysisProcess, Long> {
    Optional<AnalysisProcess> findByProcessId(Long processId);
}
