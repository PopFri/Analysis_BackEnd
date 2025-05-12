package popfriAnalysis.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.repository.ConditionRepository;

@Service
@RequiredArgsConstructor
public class ConditionService {
    private final ConditionRepository conditionRepository;
}
