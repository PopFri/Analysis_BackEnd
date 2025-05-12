package popfriAnalysis.spring.apiPayload.exception.handler;

import popfriAnalysis.spring.apiPayload.code.BaseErrorCode;
import popfriAnalysis.spring.apiPayload.exception.GeneralException;

public class ColumnHandler extends GeneralException {

    public ColumnHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
