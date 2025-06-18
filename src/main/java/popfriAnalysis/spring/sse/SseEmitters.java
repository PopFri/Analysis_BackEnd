package popfriAnalysis.spring.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import popfriAnalysis.spring.apiPayload.code.status.ErrorStatus;
import popfriAnalysis.spring.apiPayload.exception.handler.SseHandler;
import popfriAnalysis.spring.web.dto.ResultResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseEmitters {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final SseService sseService;

    SseEmitter add(SseEmitter emitter) {
        this.emitters.add(emitter);
        log.info("new emitter added: {}", emitter);
        log.info("emitter list size: {}", emitters.size());
        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            this.emitters.remove(emitter);    // 만료되면 리스트에서 삭제
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });

        return emitter;
    }

    public void getActivity(){
        ResultResponse.getDailyActivityDto dto = sseService.getDailyActivity();

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("dailyActivity")
                        .data(dto));
            } catch (IOException e) {
                throw new SseHandler(ErrorStatus._SSE_ERROR);
            }
        });
    }

    public void getDataCntGraph(){
        Map<LocalDateTime, Long> result = sseService.getDataCntGraph();

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("dataCntGraph")
                        .data(result));
            } catch (IOException e) {
                throw new SseHandler(ErrorStatus._SSE_ERROR);
            }
        });
    }

    public void getProcessGraph(){
        List<ResultResponse.getProcessGraphDto> result = sseService.getProcessGraph();

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("processGraph")
                        .data(result));
            } catch (IOException e) {
                throw new SseHandler(ErrorStatus._SSE_ERROR);
            }
        });
    }
}
