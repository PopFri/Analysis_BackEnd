package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.AnalysisColumn;

public interface ColumnRepository extends JpaRepository<AnalysisColumn, Long> {
}
