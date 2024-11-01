package io.nugulticket.dummy.dto;

import com.github.javafaker.Faker;
import com.github.javafaker.service.RandomService;
import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DummyEventRequestFactory {
    private final int PRICE_OFFSET = 25000;

    private final int MIN_A_SEAT_PRICE = PRICE_OFFSET;
    private final int MAX_A_SEAT_PRICE = MIN_A_SEAT_PRICE + PRICE_OFFSET;

    private final int MIN_S_SEAT_PRICE = MAX_A_SEAT_PRICE;
    private final int MAX_S_SEAT_PRICE = MIN_S_SEAT_PRICE  + PRICE_OFFSET;

    private final int MIN_R_SEAT_PRICE = MAX_S_SEAT_PRICE;
    private final int MAX_R_SEAT_PRICE = MIN_R_SEAT_PRICE + PRICE_OFFSET;

    private final int MIN_VIP_SEAT_PRICE = MAX_R_SEAT_PRICE;
    private final int MAX_VIP_SEAT_PRICE = MIN_VIP_SEAT_PRICE + PRICE_OFFSET;

    private final int MIN_SEAT_COUNT = 5;
    private final int MAX_SEAT_COUNT = 20;

    private final List<String> CATEGORI = new ArrayList<>(){{
        add("뮤지컬");
        add("영화");
        add("공연");
    }};

    private CreateEventRequest createEventRequest(Faker faker, int size) {
        RandomService randomService = faker.random();
        String categori = CATEGORI.get(randomService.nextInt(0, CATEGORI.size() - 1));
        int runtime = randomService.nextInt(100, 200);
        int viewRating = randomService.nextInt(1, size);
        double rating = randomService.nextInt(0, 5);
        int vipSeatCount = randomService.nextInt(MIN_SEAT_COUNT, MAX_SEAT_COUNT);
        int rSeatCount = randomService.nextInt(MIN_SEAT_COUNT, MAX_SEAT_COUNT);
        int sSeatCount = randomService.nextInt(MIN_SEAT_COUNT, MAX_SEAT_COUNT);
        int aSeatCount = randomService.nextInt(MIN_SEAT_COUNT, MAX_SEAT_COUNT);
        int vipSeatPrice = randomService.nextInt(MIN_VIP_SEAT_PRICE, MAX_VIP_SEAT_PRICE);
        int rSeatPrice = randomService.nextInt(MIN_R_SEAT_PRICE, MAX_R_SEAT_PRICE);
        int sSeatPrice = randomService.nextInt(MIN_S_SEAT_PRICE, MAX_S_SEAT_PRICE);
        int aSeatPrice = randomService.nextInt(MIN_A_SEAT_PRICE, MAX_A_SEAT_PRICE);

        return CreateEventRequest.builder()
                .title(faker.book().title())
                .place(faker.address().city())
                .category(categori)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .runtime(Integer.toString(runtime))
                .viewRating(Integer.toString(viewRating))
                .rating(rating)
                .bookAble(true)
                .vipSeatCount(vipSeatCount)
                .rSeatCount(rSeatCount)
                .sSeatCount(sSeatCount)
                .aSeatCount(aSeatCount)
                .vipSeatPrice(vipSeatPrice)
                .rSeatPrice(rSeatPrice)
                .sSeatPrice(sSeatPrice)
                .aSeatPrice(aSeatPrice)
                .build();
    }

    public List<CreateEventRequest> createEventsRequests(Faker faker, int size) {
        List<CreateEventRequest> requests = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            requests.add(createEventRequest(faker, size));
        }

        return requests;
    }
}
