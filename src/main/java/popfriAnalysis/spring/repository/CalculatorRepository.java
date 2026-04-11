package popfriAnalysis.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import popfriAnalysis.spring.domain.AnalysisProcess;
import popfriAnalysis.spring.domain.Calculator;

import java.util.List;

public interface CalculatorRepository extends JpaRepository<Calculator, Long> {
    List<Calculator> findByProcess(AnalysisProcess process);

    @Query("SELECT c FROM Calculator c LEFT JOIN FETCH c.condition cond LEFT JOIN FETCH cond.column WHERE c.process = :process ORDER BY c.calIndex")
    List<Calculator> findByProcessWithConditionAndColumn(@Param("process") AnalysisProcess process);
}
