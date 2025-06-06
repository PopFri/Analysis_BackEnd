package popfriAnalysis.spring.apiPayload.exception.handler;

import popfriAnalysis.spring.apiPayload.code.BaseErrorCode;
import popfriAnalysis.spring.apiPayload.exception.GeneralException;

public class ResultHandler extends GeneralException {

    public ResultHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
