package popfriAnalysis.spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.AnalysisColumn;
import popfriAnalysis.spring.domain.AnalysisFail;

import java.util.List;

public interface FailRepository extends JpaRepository<AnalysisFail, Long> {
    Page<AnalysisFail> findByColumnIn(List<AnalysisColumn> columns, Pageable pageable);
}
