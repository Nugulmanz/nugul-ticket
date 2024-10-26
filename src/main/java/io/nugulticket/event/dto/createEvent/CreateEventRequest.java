package io.nugulticket.event.dto.createEvent;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateEventRequest {
    private String title;
    private String place;
    private String category;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    //상영시간
    private String runtime;
    private String viewRating;
    private Double rating;
    private Boolean bookAble;
    private int vipSeatCount;
    private int rSeatCount;
    private int sSeatCount;
    private int aSeatCount;

    private int vipSeatPrice;
    private int rSeatPrice;
    private int sSeatPrice;
    private int aSeatPrice;
}
