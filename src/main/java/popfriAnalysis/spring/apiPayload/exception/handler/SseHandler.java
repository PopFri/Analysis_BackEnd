package popfriAnalysis.spring.apiPayload.exception.handler;

import popfriAnalysis.spring.apiPayload.code.BaseErrorCode;
import popfriAnalysis.spring.apiPayload.exception.GeneralException;

public class SseHandler extends GeneralException {

    public SseHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
