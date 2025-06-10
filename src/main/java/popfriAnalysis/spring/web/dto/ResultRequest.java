package popfriAnalysis.spring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class ResultRequest {
    @Getter
    @Schema(title = "RESULT_REQ_01 : 분석 결과 조회 요청 DTO")
    public static class getResultColumnDto{
        Long processId;
        String columnName;
        String order;
    }
}
