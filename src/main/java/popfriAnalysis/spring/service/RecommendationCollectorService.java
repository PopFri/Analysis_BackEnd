package popfriAnalysis.spring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.domain.LogData;
import popfriAnalysis.spring.domain.UserMovieEventLog;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 영화 추천에 필요한 컬럼을 Kafka 메시지에서 수집하는 고정 프로세스.
 * 필수 컬럼이 모두 존재하는 메시지만 user_movie_event_log 테이블에 저장한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationCollectorService {

    private static final Set<String> REQUIRED_COLUMNS = Set.of(
            "QUERY_uid",
            "QUERY_e_c",
            "QUERY_e_a",
            "QUERY_e_n",
            "QUERY_e_v",
            "QUERY_dimension1",
            "QUERY_dimension2",
            "QUERY_dimension3",
            "QUERY_dimension4"
    );

    private final JdbcTemplate jdbcTemplate;

    /**
     * 메시지 배치에서 수집 가능한 항목만 추려 bulk insert 한다.
     */
    public void collectBatch(List<JSONObject> jsonObjects, List<LogData> logDataList) {
        List<UserMovieEventLog> batch = new ArrayList<>();

        for (int i = 0; i < jsonObjects.size(); i++) {
            JSONObject json = jsonObjects.get(i);
            if (!hasAllRequiredColumns(json)) {
                log.debug("Recommendation collect skipped (missing columns): logId={}",
                        logDataList.get(i).getLogId());
                continue;
            }

            batch.add(UserMovieEventLog.builder()
                    .uid(getString(json, "QUERY_uid"))
                    .eventCategory(getString(json, "QUERY_e_c"))
                    .eventAction(getString(json, "QUERY_e_a"))
                    .eventName(getString(json, "QUERY_e_n"))
                    .eventValue(getLong(json, "QUERY_e_v"))
                    .movieTitle(getString(json, "QUERY_dimension1"))
                    .userBirth(getString(json, "QUERY_dimension2"))
                    .userGender(getString(json, "QUERY_dimension3"))
                    .movieId(getLong(json, "QUERY_dimension4"))
                    .logData(logDataList.get(i))
                    .build());
        }

        if (!batch.isEmpty()) {
            bulkInsert(batch);
            log.info("Recommendation collect: saved {}/{} messages", batch.size(), jsonObjects.size());
        }
    }

    private boolean hasAllRequiredColumns(JSONObject json) {
        for (String col : REQUIRED_COLUMNS) {
            Object value = json.get(col);
            if (value == null || value.toString().isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String getString(JSONObject json, String key) {
        Object value = json.get(key);
        return value != null ? value.toString() : null;
    }

    private Long getLong(JSONObject json, String key) {
        Object value = json.get(key);
        if (value == null) return null;
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            log.warn("eventValue parse failed (key={}): {}", key, value);
            return null;
        }
    }

    private void bulkInsert(List<UserMovieEventLog> list) {
        String sql = """
                INSERT INTO user_movie_event_log
                    (uid, event_category, event_action, event_name, event_value,
                     movie_title, user_birth, user_gender, movie_id, log_id, createdAt, updatedAt)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.batchUpdate(sql, list, list.size(), (ps, item) -> {
            ps.setString(1, item.getUid());
            ps.setString(2, item.getEventCategory());
            ps.setString(3, item.getEventAction());
            ps.setString(4, item.getEventName());
            ps.setObject(5, item.getEventValue());
            ps.setString(6, item.getMovieTitle());
            ps.setString(7, item.getUserBirth());
            ps.setString(8, item.getUserGender());
            ps.setObject(9, item.getMovieId());
            ps.setLong(10, item.getLogData().getLogId());
            ps.setTimestamp(11, Timestamp.valueOf(now));
            ps.setTimestamp(12, Timestamp.valueOf(now));
        });
    }
}
