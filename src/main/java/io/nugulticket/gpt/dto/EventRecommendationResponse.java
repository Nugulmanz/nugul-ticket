package io.nugulticket.gpt.dto;

import lombok.Getter;

@Getter
public class EventRecommendationResponse {
    private Long eventId;
    private String title;
    private String category;

    public EventRecommendationResponse(Long eventId, String title, String category) {
        this.eventId = eventId;
        this.title = title;
        this.category = category;
    }
}
