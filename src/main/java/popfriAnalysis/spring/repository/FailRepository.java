package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import popfriAnalysis.spring.domain.AnalysisFail;

import java.time.LocalDateTime;

public interface FailRepository extends JpaRepository<AnalysisFail, Long> {
    @Query(value = """
        SELECT COUNT(DISTINCT log_id)
        FROM analysis_fail
        WHERE createdAt >= :start AND createdAt < :end
        """, nativeQuery = true)
    Long countDistinctLogDataCreatedToday(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
