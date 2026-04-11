package popfriAnalysis.spring.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.domain.AnalysisProcess;
import popfriAnalysis.spring.domain.Calculator;
import popfriAnalysis.spring.sse.SseEmitters;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaResultFacade {

    private final ResultService resultService;
    private final SseEmitters sseEmitters;
    private final MeterRegistry meterRegistry;

    @KafkaListener(topics = "matomo-log", groupId = "analysis_server_consumer_01")
    public void consume(List<String> messages) {
        Counter.builder("kafka.messages.processed")
                .description("실제 처리된 Kafka 메시지 건수")
                .register(meterRegistry)
                .increment(messages.size());

        List<AnalysisProcess> processes = resultService.getProcesses();
        Map<Long, List<Calculator>> calculatorMap = resultService.getCalculatorMap(processes);

        JSONParser jsonParser = new JSONParser();
        for (int i = 0; i < messages.size(); i++) {
            String message = messages.get(i);
            JSONObject jsonObject;
            try {
                jsonObject = (JSONObject) jsonParser.parse(message);
            } catch (ParseException e) {
                log.error("Failed to parse Kafka message: {}", message, e);
                throw new BatchListenerFailedException("JSON 파싱 실패", e, i);
            }
            try {
                resultService.saveOneMessage(jsonObject, message, processes, calculatorMap);
            } catch (Exception e) {
                log.error("saveOneMessage failed at index {}: {}", i, message, e);
                throw new BatchListenerFailedException("saveOneMessage 실패", e, i);
            }
        }

        sseEmitters.getActivity();
        sseEmitters.getDataCntGraph();
        sseEmitters.getProcessGraph();
    }
}
