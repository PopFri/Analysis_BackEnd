package popfriAnalysis.spring.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import popfriAnalysis.spring.repository.LogDataRepository;
import popfriAnalysis.spring.repository.ProcessRepository;
import popfriAnalysis.spring.web.dto.ResultResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SseService {
    private final LogDataRepository logDataRepository;
    private final ProcessRepository processRepository;

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
    public record TimeRange(LocalDateTime start, LocalDateTime end) {}
    public Map<LocalDateTime, Long> getDataCntGraph(){
        LocalDateTime now = LocalDateTime.now();

        // 현재 시각을 5분 단위로 내림
        int minute = now.getMinute();
        int roundedMinute = (minute / 5) * 5;
        LocalDateTime end = now.withMinute(roundedMinute).withSecond(0).withNano(0);

        // 시작 시각은 1시간 전의 5분 단위로 맞춤
        LocalDateTime start = end.minusHours(1);

        List<TimeRange> intervals = new ArrayList<>();
        for (LocalDateTime t = start; t.isBefore(end); t = t.plusMinutes(5)) {
            intervals.add(new TimeRange(t, t.plusMinutes(5)));
        }
        intervals.add(new TimeRange(end, end.plusMinutes(5)));

        Map<LocalDateTime, Long> result = new LinkedHashMap<>();
        for (TimeRange range : intervals) {
            long count = logDataRepository.countByCreatedAtBetween(range.start(), range.end());
            result.put(range.start, count);
        }

        return result;
    }

    @Transactional
    public List<ResultResponse.getProcessGraphDto> getProcessGraph(){
        return processRepository.findAll().stream().map(process -> {
            Integer successCnt = process.getColumnList().get(0).getSuccessList().size();
            Integer failCnt = process.getColumnList().get(0).getFailList().size();

            return ResultResponse.getProcessGraphDto.builder()
                    .processId(process.getProcessId())
                    .processName(process.getName())
                    .successCnt(successCnt)
                    .failCnt(failCnt)
                    .build();
        }).toList();
    }
}
