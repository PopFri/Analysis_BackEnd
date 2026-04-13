package popfriAnalysis.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import popfriAnalysis.spring.repository.UserMovieEventLogRepository;
import popfriAnalysis.spring.web.dto.ResultResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 영화 분석 통계 서비스.
 * type 값: "default" | "male" | "female" | "10" | "20" | "30" | "40"
 * date 값: "day" (일간) | "week" (주간) | "month" (월간)
 */
@Service
@RequiredArgsConstructor
public class MovieAnalysisService {

    private final UserMovieEventLogRepository eventLogRepository;

    // ── 선호 영화 (방문 횟수) ────────────────────────────────────────────

    public List<ResultResponse.MovieStatDto> getVisitStat(String date, String type) {
        LocalDateTime[] range = dateRange(date);
        return switch (classifyType(type)) {
            case DEFAULT -> eventLogRepository.findVisitStatDefault(range[0], range[1]);
            case GENDER  -> eventLogRepository.findVisitStatByGender(range[0], range[1], type.toUpperCase());
            case AGE     -> {
                int[] ages = ageRange(type);
                yield eventLogRepository.findVisitStatByAge(range[0], range[1], ages[0], ages[1]);
            }
        };
    }

    // ── 체류시간 합산 ────────────────────────────────────────────────────

    public List<ResultResponse.MovieStatDto> getDwellTimeStat(String date, String type) {
        LocalDateTime[] range = dateRange(date);
        return switch (classifyType(type)) {
            case DEFAULT -> eventLogRepository.findDwellTimeStatDefault(range[0], range[1]);
            case GENDER  -> eventLogRepository.findDwellTimeStatByGender(range[0], range[1], type.toUpperCase());
            case AGE     -> {
                int[] ages = ageRange(type);
                yield eventLogRepository.findDwellTimeStatByAge(range[0], range[1], ages[0], ages[1]);
            }
        };
    }

    // ── uid 목록 ─────────────────────────────────────────────────────────

    public List<String> getAllUids() {
        return eventLogRepository.findAllUids();
    }

    // ── 사용자별 선호 장르 (체류시간 Top 3) ─────────────────────────────────

    public List<ResultResponse.UserGenreStatDto> getTopGenresByUid(String uid) {
        return eventLogRepository.findTopGenresByUid(uid, PageRequest.of(0, 3));
    }

    // ── 헬퍼 ─────────────────────────────────────────────────────────────

    /** date 파라미터를 기준으로 [시작, 종료) LocalDateTime 범위 반환 */
    public LocalDateTime[] dateRange(String date) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = switch (date) {
            case "week"  -> today.minusDays(6).atStartOfDay();
            case "month" -> today.minusDays(29).atStartOfDay();
            default      -> today.atStartOfDay();   // "day" – 오늘 하루
        };
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        return new LocalDateTime[]{start, end};
    }

    private TypeKind classifyType(String type) {
        if ("default".equals(type)) return TypeKind.DEFAULT;
        if ("male".equals(type) || "female".equals(type)) return TypeKind.GENDER;
        return TypeKind.AGE;
    }

    private int[] ageRange(String agePrefix) {
        int base = Integer.parseInt(agePrefix);
        return new int[]{base, base + 9};
    }

    private enum TypeKind { DEFAULT, GENDER, AGE }
}
