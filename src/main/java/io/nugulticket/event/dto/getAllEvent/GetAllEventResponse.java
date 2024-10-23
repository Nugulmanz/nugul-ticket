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
  //  private String eventPeriod;

    public GetAllEventResponse(Event event) {
        this.title = event.getTitle();
        this.place = event.getPlace();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
//        this.eventPeriod = event.getStartDate() + " ~ " + event.getEndDate();
    }
}
