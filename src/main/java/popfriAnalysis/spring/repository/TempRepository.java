package popfriAnalysis.spring.repository;

import popfriAnalysis.spring.domain.Temp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempRepository extends JpaRepository<Temp, Long> {
}
