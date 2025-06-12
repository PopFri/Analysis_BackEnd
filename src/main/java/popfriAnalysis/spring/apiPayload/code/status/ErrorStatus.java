package popfriAnalysis.spring.apiPayload.code.status;

import popfriAnalysis.spring.apiPayload.code.BaseErrorCode;
import popfriAnalysis.spring.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    //Column Error
    _NOT_EXIST_COLUMN(HttpStatus.BAD_REQUEST,"COLUMN4001","존재하지 않은 컬럼입니다."),

    //Process Error
    _NOT_EXIST_PROCESS(HttpStatus.BAD_REQUEST,"PROCESS4001","존재하지 않은 프로세스입니다."),

    //Condition Error
    _NOT_EXIST_RELATION(HttpStatus.BAD_REQUEST,"CONDITION4001","존재하지 않은 관계연산자입니다."),
    _NOT_EXIST_OPERATION(HttpStatus.BAD_REQUEST,"CONDITION4002","존재하지 않은 조건연산자입니다."),
    _CALCULATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR,"CONDITION5001","조건문 연산을 실패하였습니다."),

    //Result Error
    _NOT_EXIST_SORT(HttpStatus.BAD_REQUEST,"RESULT4001","존재하지 않은 정렬방식입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}