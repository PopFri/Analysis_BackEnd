package popfriAnalysis.spring.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.repository.LogDataRepository;
import popfriAnalysis.spring.web.dto.ResultResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SseService {
    private final LogDataRepository logDataRepository;

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
}
