package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import popfriAnalysis.spring.domain.UserMovieEventLog;
import popfriAnalysis.spring.web.dto.ResultResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface UserMovieEventLogRepository extends JpaRepository<UserMovieEventLog, Long> {

    // ── 선호 영화 (방문 횟수) ────────────────────────────────────────────

    /** 전체 – 기간 내 영화별 방문 횟수 */
    @Query("""
        SELECT new popfriAnalysis.spring.web.dto.ResultResponse$MovieStatDto(u.movieTitle, COUNT(u))
        FROM UserMovieEventLog u
        WHERE u.createdAt BETWEEN :start AND :end
        GROUP BY u.movieTitle
        ORDER BY COUNT(u) DESC
        """)
    List<ResultResponse.MovieStatDto> findVisitStatDefault(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /** 성별 필터 – 기간 내 영화별 방문 횟수 */
    @Query("""
        SELECT new popfriAnalysis.spring.web.dto.ResultResponse$MovieStatDto(u.movieTitle, COUNT(u))
        FROM UserMovieEventLog u
        WHERE u.createdAt BETWEEN :start AND :end
          AND u.userGender = :gender
        GROUP BY u.movieTitle
        ORDER BY COUNT(u) DESC
        """)
    List<ResultResponse.MovieStatDto> findVisitStatByGender(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("gender") String gender);

    /** 연령대 필터 – 기간 내 영화별 방문 횟수 (생일 4자리 연도 기준) */
    @Query("""
        SELECT new popfriAnalysis.spring.web.dto.ResultResponse$MovieStatDto(u.movieTitle, COUNT(u))
        FROM UserMovieEventLog u
        WHERE u.createdAt BETWEEN :start AND :end
          AND (YEAR(CURRENT_DATE) - CAST(SUBSTRING(u.userBirth, 1, 4) AS int)) BETWEEN :ageMin AND :ageMax
        GROUP BY u.movieTitle
        ORDER BY COUNT(u) DESC
        """)
    List<ResultResponse.MovieStatDto> findVisitStatByAge(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("ageMin") int ageMin,
            @Param("ageMax") int ageMax);

    // ── 체류시간 합산 ────────────────────────────────────────────────────

    /** 전체 – 기간 내 영화별 체류시간(초) 합산 */
    @Query("""
        SELECT new popfriAnalysis.spring.web.dto.ResultResponse$MovieStatDto(u.movieTitle, SUM(u.eventValue))
        FROM UserMovieEventLog u
        WHERE u.createdAt BETWEEN :start AND :end
          AND u.eventAction = 'time_spent'
        GROUP BY u.movieTitle
        ORDER BY SUM(u.eventValue) DESC
        """)
    List<ResultResponse.MovieStatDto> findDwellTimeStatDefault(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /** 성별 필터 – 기간 내 영화별 체류시간(초) 합산 */
    @Query("""
        SELECT new popfriAnalysis.spring.web.dto.ResultResponse$MovieStatDto(u.movieTitle, SUM(u.eventValue))
        FROM UserMovieEventLog u
        WHERE u.createdAt BETWEEN :start AND :end
          AND u.eventAction = 'time_spent'
          AND u.userGender = :gender
        GROUP BY u.movieTitle
        ORDER BY SUM(u.eventValue) DESC
        """)
    List<ResultResponse.MovieStatDto> findDwellTimeStatByGender(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("gender") String gender);

    /** 연령대 필터 – 기간 내 영화별 체류시간(초) 합산 */
    @Query("""
        SELECT new popfriAnalysis.spring.web.dto.ResultResponse$MovieStatDto(u.movieTitle, SUM(u.eventValue))
        FROM UserMovieEventLog u
        WHERE u.createdAt BETWEEN :start AND :end
          AND u.eventAction = 'time_spent'
          AND (YEAR(CURRENT_DATE) - CAST(SUBSTRING(u.userBirth, 1, 4) AS int)) BETWEEN :ageMin AND :ageMax
        GROUP BY u.movieTitle
        ORDER BY SUM(u.eventValue) DESC
        """)
    List<ResultResponse.MovieStatDto> findDwellTimeStatByAge(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("ageMin") int ageMin,
            @Param("ageMax") int ageMax);

    // ── uid 목록 ─────────────────────────────────────────────────────────

    /** 전체 uid 목록 (중복 제거) */
    @Query("SELECT DISTINCT u.uid FROM UserMovieEventLog u ORDER BY u.uid")
    List<String> findAllUids();

    // ── 사용자별 장르 체류시간 Top 3 ─────────────────────────────────────

    /** uid 기준 장르별 체류시간(초) 합산 상위 3개 */
    @Query("""
        SELECT new popfriAnalysis.spring.web.dto.ResultResponse$UserGenreStatDto(u.eventCategory, SUM(u.eventValue))
        FROM UserMovieEventLog u
        WHERE u.uid = :uid
          AND u.eventAction = 'time_spent'
        GROUP BY u.eventCategory
        ORDER BY SUM(u.eventValue) DESC
        """)
    List<ResultResponse.UserGenreStatDto> findTopGenresByUid(@Param("uid") String uid,
                                                              org.springframework.data.domain.Pageable pageable);
}
