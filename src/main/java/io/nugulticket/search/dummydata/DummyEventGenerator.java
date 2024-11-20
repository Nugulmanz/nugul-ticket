package io.nugulticket.search.dummydata;

import io.nugulticket.search.entity.EventDocument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Event 클래스를 사용해 더미 데이터를 생성하는 클래스
 */
public class DummyEventGenerator {

    private static final Random RANDOM = new Random();
    private static final String[] CATEGORIES = {
            "뮤지컬", "연극", "콘서트", "오페라", "팬미팅"
    };
    private static final String[] KOREAN_WORDS = {
            "해피", "감동", "사랑", "이야기", "모험", "여행", "우정", "행복", "꿈"
    };
    private static final String[] DESCRIPTIONS = {
            "흥미진진한 ", "재밌는 ", "슬픈 ", "감동적인 ", "웃긴 ", "감동실화! ", "무서운 "
    };
    private static final String[] PLACES = {
            "예술의 전당", "충무아트센터", "샤롯데씨어터", "블루스퀘어 마스터카드홀", "고척스카이돔", "장충체육관", "KT&G 상상마당 라이브홀"
    };

    private static final AtomicLong counter = new AtomicLong(1);

    private static Long getNextId() {
        return counter.getAndIncrement(); // 1씩 증가
    }

    public static EventDocument createDummyEvent() {
        Long eventId = DummyEventGenerator.getNextId(); // 순차적으로 ID가 반환됨
        String title = generateRandomKoreanTitle();
        LocalDate startDate = LocalDate.now().plusDays(RANDOM.nextInt(30)); // 오늘부터 30일 이내의 시작 날짜
        LocalDate endDate = startDate.plusDays(RANDOM.nextInt(5) + 1); // 시작 날짜 이후 1~5일 이내의 종료 날짜
        String place = PLACES[RANDOM.nextInt(PLACES.length)];
        String category = CATEGORIES[RANDOM.nextInt(CATEGORIES.length)];
        String description = DESCRIPTIONS[RANDOM.nextInt(DESCRIPTIONS.length)] + title;
        String runtime = RANDOM.nextInt(180) + "분"; // 0-180분 랜덤 런타임
        String viewRating = "PG";

        // 0.0-5.0 사이의 랜덤 평점 생성 후 소수점 한자리로 제한
        Double rating = Math.round(RANDOM.nextDouble() * 50.0) / 10.0; // 0.0-5.0 사이의 소수점 한 자리 랜덤 평점 생성

        Boolean bookAble = RANDOM.nextBoolean();
        String imageUrl = "dummyImageUrl";

        // ISO 8601 형식으로 변환
        String formattedStartDate = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String formattedEndDate = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        return new EventDocument(eventId, category, title, description, formattedStartDate, formattedEndDate, runtime, viewRating, rating, place, bookAble, imageUrl);

    }

    private static String generateRandomKoreanTitle() {
        StringBuilder titleBuilder = new StringBuilder();
        int wordCount = RANDOM.nextInt(2) + 1; // 1 또는 2개의 단어로 구성
        for (int i = 0; i < wordCount; i++) {
            titleBuilder.append(KOREAN_WORDS[RANDOM.nextInt(KOREAN_WORDS.length)]);
            if (i < wordCount - 1) {
                titleBuilder.append(" "); // 단어 사이에 공백 추가
            }
        }
        return titleBuilder.toString();
    }


}
