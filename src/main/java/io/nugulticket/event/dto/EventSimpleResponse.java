package io.nugulticket.event.dto;

import io.nugulticket.event.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventSimpleResponse {
    LocalDate beginDate;
    LocalDate endDate;
    String title;
    String place;

    public static EventSimpleResponse of(Event event) {
        return new EventSimpleResponse(event.getStartDate(), event.getEndDate(),  event.getTitle(), event.getPlace());
    }
}
