package popfriAnalysis.spring.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import popfriAnalysis.spring.service.ConditionService;

@RestController
@RequestMapping("/api/v1/condition")
@RequiredArgsConstructor
@Tag(name = "Condition", description = "Condition 관련 API입니다.")
public class ConditionController {
    private final ConditionService conditionService;

}
