package popfriAnalysis.spring.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import popfriAnalysis.spring.apiPayload.ApiResponse;
import popfriAnalysis.spring.service.MovieAnalysisService;
import popfriAnalysis.spring.web.dto.ResultResponse;

import java.util.List;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
@Tag(name = "MovieAnalysis", description = "영화 분석 통계 API")
public class MovieAnalysisController {

    private final MovieAnalysisService movieAnalysisService;

    @GetMapping("/visit")
    @Operation(
        summary = "선호 영화 통계",
        description = "date: day|week|month / type: default|male|female|10|20|30|40"
    )
    public ApiResponse<List<ResultResponse.MovieStatDto>> getVisitStat(
            @RequestParam(defaultValue = "day") String date,
            @RequestParam(defaultValue = "default") String type) {
        return ApiResponse.onSuccess(movieAnalysisService.getVisitStat(date, type));
    }

    @GetMapping("/dwell-time")
    @Operation(
        summary = "체류시간 통계",
        description = "date: day|week|month / type: default|male|female|10|20|30|40"
    )
    public ApiResponse<List<ResultResponse.MovieStatDto>> getDwellTimeStat(
            @RequestParam(defaultValue = "day") String date,
            @RequestParam(defaultValue = "default") String type) {
        return ApiResponse.onSuccess(movieAnalysisService.getDwellTimeStat(date, type));
    }

    @GetMapping("/user-genre")
    @Operation(
        summary = "사용자 선호 장르 Top 3",
        description = "uid를 기준으로 체류시간이 가장 높은 장르 Top 3 반환"
    )
    public ApiResponse<List<ResultResponse.UserGenreStatDto>> getTopGenresByUid(
            @RequestParam String uid) {
        return ApiResponse.onSuccess(movieAnalysisService.getTopGenresByUid(uid));
    }
}
