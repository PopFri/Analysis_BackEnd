package popfriAnalysis.spring.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import popfriAnalysis.spring.apiPayload.ApiResponse;
import popfriAnalysis.spring.domain.AnalysisProcess;
import popfriAnalysis.spring.service.ProcessService;
import popfriAnalysis.spring.service.ResultService;
import popfriAnalysis.spring.web.dto.ResultResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/result")
@RequiredArgsConstructor
@Tag(name = "Result", description = "Result 관련 API입니다.")
public class ResultController {
    private final ProcessService processService;
    private final ResultService resultService;

    @GetMapping("")
    @Operation(summary = "분석 결과 조회", description = "프로세스 아이디를 입력받아 해당 프로세스 내 분석 결과 반환")
    public ApiResponse<List<ResultResponse.getResultColumn>> getResultColumnList(@RequestParam Long processId){
        AnalysisProcess analysisProcess = processService.getProcess(processId);

        return ApiResponse.onSuccess(resultService.getResultList(analysisProcess));
    }

    @GetMapping("/success")
    @Operation(summary = "성공 데이터 결과 조회", description = "프로세스 아이디를 입력받아 조건 별 성공 데이터 수 반환")
    public ApiResponse<ResultResponse.successDataCountDto> getResultSuccessDataToGraph(@RequestParam Long processId){

        return ApiResponse.onSuccess(resultService.successDataCountByCondition(processId));
    }
}
