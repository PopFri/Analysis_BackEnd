package popfriAnalysis.spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.AnalysisColumn;
import popfriAnalysis.spring.domain.AnalysisSuccess;

import java.util.List;

public interface SuccessRepository extends JpaRepository<AnalysisSuccess, Long> {

    Page<AnalysisSuccess> findByColumnIn(List<AnalysisColumn> columns, Pageable pageable);
}
