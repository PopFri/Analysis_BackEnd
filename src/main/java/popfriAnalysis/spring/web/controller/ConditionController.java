package popfriAnalysis.spring.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import popfriAnalysis.spring.apiPayload.ApiResponse;
import popfriAnalysis.spring.domain.AnalysisProcess;
import popfriAnalysis.spring.service.ConditionService;
import popfriAnalysis.spring.service.ProcessService;
import popfriAnalysis.spring.web.dto.ColumnRequest;
import popfriAnalysis.spring.web.dto.ConditionRequest;

@RestController
@RequestMapping("/api/v1/condition")
@RequiredArgsConstructor
@Tag(name = "Condition", description = "Condition 관련 API입니다.")
public class ConditionController {
    private final ConditionService conditionService;
    private final ProcessService processService;

    @PostMapping("")
    @Operation(summary = "분석 조건 추가", description = "프로세스 아이디와 추가할 조건을 입력받아 저장")
    public ApiResponse<Boolean> addAnalysisColumn(@RequestBody ConditionRequest.AddAnalysisColumnDto request){
        AnalysisProcess analysisProcess = processService.getProcess(request.getProcessId());

        conditionService.delAnalysisConditionAll(analysisProcess);
        return ApiResponse.onSuccess(conditionService.addAnalysisCondition(analysisProcess, request.getCondition()));
    }
}
