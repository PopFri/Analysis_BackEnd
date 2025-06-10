package popfriAnalysis.spring.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import popfriAnalysis.spring.domain.common.BaseEntity;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "log_data")
public class LogData extends BaseEntity {
    @Id
    @Column(name = "log_id", length = 100)
    private String logId;

    @Column(name = "data", columnDefinition = "text")
    private String data;

    @OneToMany(mappedBy = "logData", cascade = CascadeType.ALL)
    private List<AnalysisSuccess> successList;

    @OneToMany(mappedBy = "logData", cascade = CascadeType.ALL)
    private List<AnalysisFail> failList;
}
