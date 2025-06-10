package popfriAnalysis.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.apiPayload.code.status.ErrorStatus;
import popfriAnalysis.spring.apiPayload.exception.handler.ProcessHandler;
import popfriAnalysis.spring.domain.AnalysisProcess;
import popfriAnalysis.spring.domain.common.BaseEntity;
import popfriAnalysis.spring.repository.ProcessRepository;
import popfriAnalysis.spring.web.dto.ProcessResponse;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessService {
    private final ProcessRepository processRepository;

    public AnalysisProcess getProcess(Long processId){
        return processRepository.findByProcessId(processId).orElseThrow(() -> new ProcessHandler(ErrorStatus._NOT_EXIST_PROCESS));
    }

    public ProcessResponse.getProcessIdDTO addAnalysisProcess(String name){
        AnalysisProcess process = processRepository.save(AnalysisProcess.builder()
                .name(name)
                .build());

        return ProcessResponse.getProcessIdDTO.builder().
                processId(process.getProcessId())
                .build();
    }

    public List<ProcessResponse.getProcessListResDTO> getProcessList(){
        return processRepository.findAll().stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedAt).reversed())
                .map(process ->
                    ProcessResponse.getProcessListResDTO.builder()
                            .processId(process.getProcessId())
                            .name(process.getName())
                            .createAt(process.getCreatedAt().toLocalDate())
                            .build())
                .toList();
    }
}
