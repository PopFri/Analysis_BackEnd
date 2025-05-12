package popfriAnalysis.spring.apiPayload.exception.handler;

import popfriAnalysis.spring.apiPayload.code.BaseErrorCode;
import popfriAnalysis.spring.apiPayload.exception.GeneralException;

public class ProcessHandler extends GeneralException {

    public ProcessHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
