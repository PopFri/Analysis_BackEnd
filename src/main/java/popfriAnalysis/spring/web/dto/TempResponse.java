package popfriAnalysis.spring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TempResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "TEMP_RES_01 : 테스트 성공 응답")
    public static class TempTestDTO{
        @Schema(description = "성공 텍스트", example = "성공입니다.")
        String testString;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "TEMP_RES_02 : 테스트 실패 응답")
    public static class TempExceptionDTO{
        @Schema(description = "입력한 플래그", example = "0")
        Integer flag;
    }
}
