package popfriAnalysis.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.apiPayload.code.status.ErrorStatus;
import popfriAnalysis.spring.apiPayload.exception.handler.ProcessHandler;
import popfriAnalysis.spring.domain.AnalysisProcess;
import popfriAnalysis.spring.repository.ProcessRepository;

@Service
@RequiredArgsConstructor
public class ProcessService {
    private final ProcessRepository processRepository;

    public AnalysisProcess getProcess(Long processId){
        return processRepository.findByProcessId(processId).orElseThrow(() -> new ProcessHandler(ErrorStatus._NOT_EXIST_PROCESS));
    }
}
