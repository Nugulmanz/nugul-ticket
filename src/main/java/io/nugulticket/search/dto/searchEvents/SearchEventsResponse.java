package io.nugulticket.search.dto.searchEvents;

import io.nugulticket.search.entity.EventDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchEventsResponse {
    private Long eventId;
    private String category;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String runtime;
    private String viewRating;
    private Double rating;
    private String place;
    private Boolean bookAble;
    private String imageUrl;

    public static SearchEventsResponse of(EventDocument eventDocument) {
        return new SearchEventsResponse(
                eventDocument.getEventId(),
                eventDocument.getCategory(),
                eventDocument.getTitle(),
                eventDocument.getDescription(),
                eventDocument.getStartDate(),
                eventDocument.getEndDate(),
                eventDocument.getRuntime(),
                eventDocument.getViewRating(),
                eventDocument.getRating(),
                eventDocument.getPlace(),
                eventDocument.getBookAble(),
                eventDocument.getImageUrl()
        );
    }


}
