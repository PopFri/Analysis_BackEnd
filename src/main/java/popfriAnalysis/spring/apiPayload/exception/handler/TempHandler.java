package popfriAnalysis.spring.apiPayload.exception.handler;

import popfriAnalysis.spring.apiPayload.code.BaseErrorCode;
import popfriAnalysis.spring.apiPayload.exception.GeneralException;

public class TempHandler extends GeneralException {

    public TempHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
