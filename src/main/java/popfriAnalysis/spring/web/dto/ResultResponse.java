package popfriAnalysis.spring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ResultResponse {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "RESULT_RES_01 : 결과 조회 응답")
    public static class getResultColumn{
        String logId;
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
}
