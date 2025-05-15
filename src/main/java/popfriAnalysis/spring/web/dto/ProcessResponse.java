package popfriAnalysis.spring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class ProcessResponse {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "PROCESS_RES_01 : 프로세스 조회 응답")
    public static class getProcessListResDTO{
        Long processId;
        String name;
        LocalDate createAt;
    }
}
