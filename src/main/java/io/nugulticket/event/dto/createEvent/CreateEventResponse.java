package io.nugulticket.event.dto.createEvent;

import io.nugulticket.event.entity.Event;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CreateEventResponse {
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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CreateEventResponse(Event event){
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
        this.createdAt = event.getCreatedAt();
        this.modifiedAt = event.getModifiedAt();
    }
}
