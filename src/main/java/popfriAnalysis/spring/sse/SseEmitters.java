package popfriAnalysis.spring.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import popfriAnalysis.spring.apiPayload.code.status.ErrorStatus;
import popfriAnalysis.spring.apiPayload.exception.handler.SseHandler;
import popfriAnalysis.spring.repository.LogDataRepository;
import popfriAnalysis.spring.web.dto.ResultResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseEmitters {
    private final LogDataRepository logDataRepository;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

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

    public ResultResponse.getDailyActivityDto getDailyActivity(){
        LocalDate today = LocalDate.now();

        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime startOfTomorrow = today.plusDays(1).atStartOfDay();
        LocalDateTime startOfYesterday = today.minusDays(1).atStartOfDay();

        Long todayCount = logDataRepository.countByCreatedAtBetween(startOfToday, startOfTomorrow);
        Long yesterdayCount = logDataRepository.countByCreatedAtBetween(startOfYesterday, startOfToday);

        double changeRate = 0.0;
        if (yesterdayCount != 0) {
            changeRate = ((double)(todayCount - yesterdayCount) / yesterdayCount) * 100.0;
        } else if (todayCount != 0) {
            changeRate = 100.0; // 어제 0건인데 오늘 데이터가 있다면 100% 증가로 처리
        }

        return ResultResponse.getDailyActivityDto.builder()
                .cnt(todayCount)
                .changeRate(changeRate)
                .build();
    }

    public void getActivity(){
        ResultResponse.getDailyActivityDto dto = getDailyActivity();

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("dailyActivity")
                        .data(dto));
            } catch (IOException e) {
                throw new SseHandler(ErrorStatus._NOT_EXIST_SORT);
            }
        });
    }
}
