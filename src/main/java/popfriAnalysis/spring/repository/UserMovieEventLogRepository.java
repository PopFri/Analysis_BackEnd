package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.UserMovieEventLog;

public interface UserMovieEventLogRepository extends JpaRepository<UserMovieEventLog, Long> {
}
