package popfriAnalysis.spring.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "calculator")
public class Calculator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calculator_id")
    private Long calculatorId;

    @Column(name = "relation", length = 100)
    private String relation;

    @Column(name = "cal_index")
    private Integer calIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private AnalysisProcess process;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "condition_id")
    private AnalysisCondition condition;
}
