package popfriAnalysis.spring.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import popfriAnalysis.spring.apiPayload.ApiResponse;
import popfriAnalysis.spring.converter.TempConverter;
import popfriAnalysis.spring.service.TempService.TempQueryService;
import popfriAnalysis.spring.web.dto.TempResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/temp")
@RequiredArgsConstructor
@Tag(name = "Test", description = "Test 관련 API입니다.")
public class TempRestController {

    private final TempQueryService tempQueryService;

    @GetMapping("/test")
    @Operation(summary = "테스트 메서드", description = "성공 응답을 반환합니다.")
    public ApiResponse<TempResponse.TempTestDTO> testAPI(){

        return ApiResponse.onSuccess(TempConverter.toTempTestDTO());
    }

    @GetMapping("/exception")
    @Operation(summary = "테스트 메서드", description = "1을 입력시 실패 응답을 반환합니다.")
    public ApiResponse<TempResponse.TempExceptionDTO> exceptionAPI(@RequestParam Integer flag){
        tempQueryService.CheckFlag(flag);
        return ApiResponse.onSuccess(TempConverter.toTempExceptionDTO(flag));
    }
}
