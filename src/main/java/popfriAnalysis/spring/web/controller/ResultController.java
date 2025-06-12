package popfriAnalysis.spring.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import popfriAnalysis.spring.apiPayload.ApiResponse;
import popfriAnalysis.spring.domain.AnalysisColumn;
import popfriAnalysis.spring.domain.AnalysisProcess;
import popfriAnalysis.spring.service.ColumnService;
import popfriAnalysis.spring.service.ProcessService;
import popfriAnalysis.spring.service.ResultService;
import popfriAnalysis.spring.web.dto.ResultRequest;
import popfriAnalysis.spring.web.dto.ResultResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/result")
@RequiredArgsConstructor
@Tag(name = "Result", description = "Result 관련 API입니다.")
public class ResultController {
    private final ProcessService processService;
    private final ResultService resultService;
    private final ColumnService columnService;

    @GetMapping("{processId}")
    @Operation(summary = "분석 결과 조회", description = "프로세스 아이디를 입력받아 해당 프로세스 내 분석 결과 반환")
    public ApiResponse<List<ResultResponse.getResultColumn>> getResultColumnList(
            @PathVariable Long processId,
            @RequestParam(value="column", required=false) String columnName,
            @RequestParam(value="order", required=false) String order){

        AnalysisProcess analysisProcess = processService.getProcess(processId);

        List<ResultResponse.getResultColumn> dtoList = resultService.getResultList(analysisProcess);

        if(columnName != null && order != null) {
            AnalysisColumn column = columnService.getColumnToName(analysisProcess, columnName);

            dtoList = resultService.sortResultList(dtoList, column.getName(), order);
        }

        return ApiResponse.onSuccess(dtoList);
    }

    @GetMapping("/success")
    @Operation(summary = "성공 데이터 결과 조회", description = "프로세스 아이디를 입력받아 조건 별 성공 데이터 수 반환")
    public ApiResponse<ResultResponse.successDataCountDto> getResultSuccessDataToGraph(@RequestParam Long processId){

        return ApiResponse.onSuccess(resultService.successDataCountByCondition(processId));
    }

    @GetMapping("/record/success")
    @Operation(summary = "성공 데이터 결과 조회", description = "프로세스 아이디를 입력받아 성공, 실패 데이터 반환")
    public ApiResponse<ResultResponse.successOrFailDataDto> getSuccessResultRecordData(@RequestParam Long processId, @RequestParam int page){

        return ApiResponse.onSuccess(resultService.successDataByProcess(processId, page, 12));
    }

    @GetMapping("/record/fail")
    @Operation(summary = "성공 데이터 결과 조회", description = "프로세스 아이디를 입력받아 성공, 실패 데이터 반환")
    public ApiResponse<ResultResponse.successOrFailDataDto> getFailResultRecordData(@RequestParam Long processId, @RequestParam int page){

        return ApiResponse.onSuccess(resultService.failDataByProcess(processId, page, 12));
    }
}
