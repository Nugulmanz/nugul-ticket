package io.nugulticket.event.dto.getAllEvent;

import io.nugulticket.event.entity.Event;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class GetAllEventResponse {

    private String title;
    private String place;
    private LocalDate startDate;
    private LocalDate endDate;

    public GetAllEventResponse(Event event) {
        this.title = event.getTitle();
        this.place = event.getPlace();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();

    }
}
