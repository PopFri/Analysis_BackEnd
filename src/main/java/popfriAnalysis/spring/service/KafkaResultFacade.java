package popfriAnalysis.spring.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.sse.SseEmitters;

import java.util.List;

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

        resultService.saveResult(messages);

        sseEmitters.getActivity();
        sseEmitters.getDataCntGraph();
        sseEmitters.getProcessGraph();
    }
}
