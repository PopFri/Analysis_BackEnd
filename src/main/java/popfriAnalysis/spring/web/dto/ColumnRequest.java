package popfriAnalysis.spring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

public class ColumnRequest {
    @Getter
    @Schema(title = "COLUMN_REQ_01 : 분석 컬럼 저장 요청 DTO")
    public static class AddAnalysisColumnDto{
        @Schema(description = "process ID", example = "1")
        Long processId;
        List<String> columnList;
    }
}
