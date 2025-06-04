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
@Table(name = "analysis_process")
public class AnalysisProcess extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "process_id")
    private Long processId;

    @Column(name = "name", length = 100)
    private String name;

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
    private List<AnalysisColumn> columnList;

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
    private List<Calculator> calculatorList;
}
