package popfriAnalysis.spring.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import popfriAnalysis.spring.domain.common.BaseEntity;

@Entity
@Getter
@Setter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user_movie_event_log")
public class UserMovieEventLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_log_id")
    private Long eventLogId;

    @Column(name = "uid", length = 500)
    private String uid;                // QUERY_uid : 사용자 고유 ID

    @Column(name = "event_category", length = 500)
    private String eventCategory;      // QUERY_e_c : 영화 장르

    @Column(name = "event_action", length = 500)
    private String eventAction;        // QUERY_e_a : 예: time_spent

    @Column(name = "event_name", length = 500)
    private String eventName;          // QUERY_e_n : 영화 제목 (이벤트 대상)

    @Column(name = "event_value", length = 500)
    private String eventValue;         // QUERY_e_v : 체류 시간(초)

    @Column(name = "movie_title", length = 500)
    private String movieTitle;         // QUERY_dimension1 : 방문한 영화 제목

    @Column(name = "user_birth", length = 500)
    private String userBirth;          // QUERY_dimension2 : 사용자 생일

    @Column(name = "user_gender", length = 500)
    private String userGender;         // QUERY_dimension3 : 사용자 성별

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id")
    private LogData logData;
}
