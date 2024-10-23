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
    private String runtime;
    private String viewRating;
    private Double rating;
    private Boolean bookAble;
}
