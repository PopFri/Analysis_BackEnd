package popfriAnalysis.spring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

public class ConditionRequest {
    @Getter
    public static class ConditionDto{
        String column;
        String op;
        String value;
        List<ConditionDto> condition;
    }
    @Getter
    @Schema(title = "COLUMN_REQ_01 : 분석 컬럼 저장 요청 DTO")
    public static class AddAnalysisConditionDto{
        Long processId;
        List<ConditionDto> condition;
    }
}
