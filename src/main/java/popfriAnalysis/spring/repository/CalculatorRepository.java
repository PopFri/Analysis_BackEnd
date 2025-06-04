package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import popfriAnalysis.spring.domain.AnalysisCondition;
import popfriAnalysis.spring.domain.AnalysisProcess;
import popfriAnalysis.spring.domain.Calculator;

import java.util.List;

public interface CalculatorRepository extends JpaRepository<Calculator, Long> {
    Calculator findByCondition(AnalysisCondition condition);
    List<Calculator> findByProcess(AnalysisProcess process);
}
