package popfriAnalysis.spring.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import popfriAnalysis.spring.apiPayload.ApiResponse;
import popfriAnalysis.spring.converter.TempConverter;
import popfriAnalysis.spring.domain.AnalysisProcess;
import popfriAnalysis.spring.service.ColumnService;
import popfriAnalysis.spring.service.ProcessService;
import popfriAnalysis.spring.web.dto.ColumnRequest;
import popfriAnalysis.spring.web.dto.TempResponse;

@RestController
@RequestMapping("/api/v1/column")
@RequiredArgsConstructor
@Tag(name = "Column", description = "Column 관련 API입니다.")
public class ColumnController {

    private final ColumnService columnService;
    private final ProcessService processService;
    @PostMapping("/")
    @Operation(summary = "분석 컬럼 추가", description = "프로세스 아이디와 추가할 컬럼 이름을 입력받아 저장")
    public ApiResponse<Boolean> addAnalysisColumn(@RequestBody ColumnRequest.AddAnalysisColumnDto request){
        AnalysisProcess analysisProcess = processService.getProcess(request.getProcessId());

        columnService.delAnalysisColumnAll(analysisProcess);
        return ApiResponse.onSuccess(columnService.addAnalysisColumn(analysisProcess, request.getColumnList()));
    }
}
