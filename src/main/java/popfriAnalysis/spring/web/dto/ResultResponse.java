package popfriAnalysis.spring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ResultResponse {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "RESULT_RES_01 : 결과 조회 응답")
    public static class getResultColumn{
        Long logId;
        List<columnDto> columnList;
        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class columnDto{
            String name;
            String value;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "RESULT_RES_02 : 필터링별 성공 데이터 갯수 응답")
    public static class successDataCountDto{
        Integer totalCount;
        List<conditionDto> conditionList;

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class conditionDto{
            String condition;
            Integer successCount;
            Integer failedCount;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "RESULT_RES_03 : 프로세스별 성공 데이터 응답")
    public static class SuccessOrFailResponseDto {
        private Long totalCount;
        private List<ResultResponse.successOrFailDataDto> data;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class successOrFailDataDto{
        List<resultDataDto> dataList;
        LocalDateTime createdAt;
        Long logId;

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class resultDataDto{
            String column;
            String value;
        }
    }
}
