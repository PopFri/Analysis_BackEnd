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
@Table(name = "analysis_column")
public class AnalysisColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "column_id")
    private Long columnId;

    @Column(name = "name", length = 500)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private AnalysisProcess process;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL)
    private List<AnalysisCondition> conditionList;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL)
    private List<AnalysisSuccess> successList;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL)
    private List<AnalysisFail> failList;
}
