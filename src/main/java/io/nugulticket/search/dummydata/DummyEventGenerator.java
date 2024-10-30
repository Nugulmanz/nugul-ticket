package io.nugulticket.search.dummydata;

import io.nugulticket.event.entity.Event;
import io.nugulticket.user.entity.User;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Event 클래스를 사용해 더미 데이터를 생성하는 클래스
 */
public class DummyEventGenerator {

    private static final String[] CATEGORIES = {"Musical", "Theater", "Concert", "Opera"};
    private static final Random RANDOM = new Random();

    // 고정된 사용자 객체 생성
    private static final User FIXED_USER = new User();


    public static Event createDummyEvent() {
        Long eventId = ThreadLocalRandom.current().nextLong(1, 1000000); // 1부터 1000000 사이의 랜덤 Long ID 생성
        String title = "Event " + RANDOM.nextInt(1000);
        LocalDate startDate = LocalDate.now().plusDays(RANDOM.nextInt(30)); // 오늘부터 30일 이내의 시작 날짜
        LocalDate endDate = startDate.plusDays(RANDOM.nextInt(5) + 1); // 시작 날짜 이후 1~5일 이내의 종료 날짜
        String place = "Place " + RANDOM.nextInt(100);
        String category = CATEGORIES[RANDOM.nextInt(CATEGORIES.length)]; // 랜덤 카테고리 선택
        String description = "Description for " + title;
        String runtime = RANDOM.nextInt(180) + "분"; // 0-180분 랜덤 런타임
        String viewRating = "PG";

        // 0.0-5.0 사이의 랜덤 평점 생성 후 소수점 한자리로 제한
        Double rating = Math.round(RANDOM.nextDouble() * 50.0) / 10.0; // 0.0-5.0 사이의 소수점 한 자리 랜덤 평점 생성

        Boolean bookAble = RANDOM.nextBoolean();
        String imageUrl = "dummyImageUrl";

        return new Event(eventId, title, description, category, startDate, endDate, runtime, viewRating, rating, place, bookAble, imageUrl);

    }



}
