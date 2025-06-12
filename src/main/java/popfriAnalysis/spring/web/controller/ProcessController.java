package popfriAnalysis.spring.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import popfriAnalysis.spring.apiPayload.ApiResponse;
import popfriAnalysis.spring.service.ProcessService;
import popfriAnalysis.spring.web.dto.ProcessRequest;
import popfriAnalysis.spring.web.dto.ProcessResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/process")
@RequiredArgsConstructor
@Tag(name = "Process", description = "Process 관련 API입니다.")
public class ProcessController {
    private final ProcessService processService;

    @PostMapping("")
    @Operation(summary = "분석 프로세스 추가", description = "프로세스 이름을 입력받아 저장")
    public ApiResponse<ProcessResponse.getProcessIdDTO> addAnalysisColumn(@RequestBody ProcessRequest.AddAnalysisProcessDto request){

        return ApiResponse.onSuccess(processService.addAnalysisProcess(request.getName()));
    }
    @GetMapping("")
    @Operation(summary = "분석 프로세스 조회", description = "분석 프로세스 리스트 반환")
    public ApiResponse<List<ProcessResponse.getProcessListResDTO>> getAnalysisColumn(){
        return ApiResponse.onSuccess(processService.getProcessList());
    }

    @DeleteMapping("")
    @Operation(summary = "분석 프로세스 삭제", description = "프로세스 삭제")
    public ApiResponse<Boolean> deleteAnalysisColumn(@RequestParam Long id){
        return ApiResponse.onSuccess(processService.deleteAnalysisProcess(id));
    }
}
