package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.LogData;

import java.time.LocalDateTime;

public interface LogDataRepository extends JpaRepository<LogData, Long > {
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
