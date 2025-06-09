package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.LogData;

public interface LogDataRepository extends JpaRepository<LogData, String > {
}
