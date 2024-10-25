package io.nugulticket.user.dto.getMyEvents;

import io.nugulticket.event.entity.Event;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class getMyEventsResponse {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String runtime;
    private String viewRating;
    private Double rating;
    private String place;
    private Boolean bookAble;

    public getMyEventsResponse(Event event) {
        this.title = event.getTitle();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.runtime = event.getRuntime();
        this.viewRating = event.getViewRating();
        this.rating = event.getRating();
        this.place = event.getPlace();
        this.bookAble = event.getBookAble();
    }
}
