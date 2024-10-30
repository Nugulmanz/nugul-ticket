package io.nugulticket.search.dto.searchEvents;

import io.nugulticket.event.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SearchEventsResponse {
    private Long eventId;
    private String category;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String runtime;
    private String viewRating;
    private Double rating;
    private String place;
    private Boolean bookAble;
    private String imageUrl;

    public static SearchEventsResponse of(Event event) {
        return new SearchEventsResponse(
                event.getEventId(),
                event.getCategory(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getRuntime(),
                event.getViewRating(),
                event.getRating(),
                event.getPlace(),
                event.getBookAble(),
                event.getImageUrl()
        );
    }


}
