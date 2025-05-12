package popfriAnalysis.spring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ColumnResponse {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "COLUMN_RES_01 : 컬럼 조회 응답")
    public static class getColumnListResDTO{
        Long columnId;
        String columnName;
    }
}
