package popfriAnalysis.spring.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "analysis_condition")
public class AnalysisCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "condition_id")
    private Long conditionId;

    @Column(name = "operator", length = 1000)
    private String operator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private AnalysisProcess process;

    @OneToMany(mappedBy = "condition", cascade = CascadeType.ALL)
    private List<AnalysisResult> resultList;
}
