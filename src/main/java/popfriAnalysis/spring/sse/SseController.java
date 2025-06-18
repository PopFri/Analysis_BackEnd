package popfriAnalysis.spring.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import popfriAnalysis.spring.web.dto.ResultResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SseController {

    private final SseEmitters sseEmitters;
    private final SseService sseService;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect() {
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);
        sseEmitters.add(emitter);
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(emitter);
    }

    @GetMapping(value = "/daily-activity", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> getActivity() {
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);
        sseEmitters.add(emitter);

        ResultResponse.getDailyActivityDto dto = sseService.getDailyActivity();

        try {
            emitter.send(SseEmitter.event()
                    .name("dailyActivity")
                    .data(dto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(emitter);
    }

    @GetMapping(value = "/data-cnt-graph", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> count() {
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);
        sseEmitters.add(emitter);

        Map<LocalDateTime, Long> dto = sseService.getDataCntGraph();

        try {
            emitter.send(SseEmitter.event()
                    .name("dataCntGraph")
                    .data(dto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(emitter);
    }
}
