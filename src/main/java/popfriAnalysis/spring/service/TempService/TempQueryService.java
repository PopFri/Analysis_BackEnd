package popfriAnalysis.spring.service.TempService;

import popfriAnalysis.spring.apiPayload.code.status.ErrorStatus;
import popfriAnalysis.spring.apiPayload.exception.handler.TempHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempQueryService {
    public void CheckFlag(Integer flag) {
        if (flag == 1)
            throw new TempHandler(ErrorStatus.TEMP_EXCEPTION);
    }
}
