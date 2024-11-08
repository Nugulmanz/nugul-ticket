package io.nugulticket.event.entity;

import io.nugulticket.common.Timestamped;
import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import io.nugulticket.event.dto.updateEvent.UpdateEventRequest;
import io.nugulticket.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "event", indexes = {
        @Index(name = "idx_event_date", columnList = "startDate, endDate"),
        @Index(name = "idx_place", columnList = "place"),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_title", columnList = "title")
})
public class Event extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "user_id")  // 공연 주최자
    private User user;

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

    private Boolean is_deleted = false;

    // S3 이미지 url
    private String imageUrl;


    public Event(User user, CreateEventRequest createEventRequest, String imageUrl) {
        this.user = user;
        this.category = createEventRequest.getCategory();
        this.title = createEventRequest.getTitle();
        this.description = createEventRequest.getDescription();
        this.startDate = createEventRequest.getStartDate();
        this.endDate = createEventRequest.getEndDate();
        this.runtime = createEventRequest.getRuntime();
        this.viewRating = createEventRequest.getViewRating();
        this.rating = createEventRequest.getRating();
        this.place = createEventRequest.getPlace();
        this.bookAble = createEventRequest.getBookAble();
        this.imageUrl = imageUrl;
    }

    public void updateEvent(UpdateEventRequest updateEventRequest, String imageUrl) {
        if (updateEventRequest.getCategory() != null) {
            this.category = updateEventRequest.getCategory();
        }
        if (updateEventRequest.getTitle() != null) {
            this.title = updateEventRequest.getTitle();
        }
        if (updateEventRequest.getDescription() != null) {
            this.description = updateEventRequest.getDescription();
        }
        if (updateEventRequest.getStartDate() != null) {
            this.startDate = updateEventRequest.getStartDate();
        }
        if (updateEventRequest.getEndDate() != null) {
            this.endDate = updateEventRequest.getEndDate();
        }
        if (updateEventRequest.getRuntime() != null) {
            this.runtime = updateEventRequest.getRuntime();
        }
        if (updateEventRequest.getViewRating() != null) {
            this.viewRating = updateEventRequest.getViewRating();
        }
        if (updateEventRequest.getRating() != null) {
            this.rating = updateEventRequest.getRating();
        }
        if (updateEventRequest.getPlace() != null) {
            this.place = updateEventRequest.getPlace();
        }
        if (updateEventRequest.getBookAble() != null) {
            this.bookAble = updateEventRequest.getBookAble();
        }
        if (updateEventRequest.getImage() != null) {
            this.imageUrl = imageUrl;
        }
    }

    public void deleteEvent(){
        this.is_deleted = true;
    }


    public Event(Long eventId, String title, String description, String category, LocalDate startDate, LocalDate endDate,
                 String runtime, String viewRating, Double rating, String place, Boolean bookAble, String dummyImageUrl) {
        this.eventId = eventId;
        this.title= title;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.runtime = runtime;
        this.viewRating = viewRating;
        this.rating = rating;
        this.place = place;
        this.bookAble = bookAble;
        this.imageUrl = dummyImageUrl;
    }

}
