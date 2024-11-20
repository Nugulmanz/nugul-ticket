package io.nugulticket.event.dto.getEvent;

import io.nugulticket.event.entity.Event;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class GetEventResponse {
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
    private String imageUrl;

    public GetEventResponse(Event event) {
        this.category = event.getCategory();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.runtime = event.getRuntime();
        this.viewRating = event.getViewRating();
        this.rating = event.getRating();
        this.place = event.getPlace();
        this.bookAble = event.getBookAble();
        this.imageUrl = event.getImageUrl();
    }
}
