package popfriAnalysis.spring.apiPayload.exception.handler;

import popfriAnalysis.spring.apiPayload.code.BaseErrorCode;
import popfriAnalysis.spring.apiPayload.exception.GeneralException;

public class ConditionHandler extends GeneralException {

    public ConditionHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
