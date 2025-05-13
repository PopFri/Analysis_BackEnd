package popfriAnalysis.spring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

public class ProcessRequest {

    @Getter
    @Schema(title = "PROCESS_REQ_01 : 분석 프로세스 저장 요청 DTO")
    public static class AddAnalysisProcessDto{
        String name;
    }
}
