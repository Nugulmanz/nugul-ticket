package io.nugulticket.event.entity;

import io.nugulticket.common.Timestamped;
import io.nugulticket.event.dto.createEvent.CreateEventRequest;
import io.nugulticket.event.dto.updateEvent.UpdateEventRequest;
import io.nugulticket.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "event")
public class Event extends Timestamped{
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

    public Event(User user,CreateEventRequest createEventRequest){
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
    }

    public void updateEvent(UpdateEventRequest updateEventRequest){
        this.category = updateEventRequest.getCategory();
        this.title = updateEventRequest.getTitle();
        this.description = updateEventRequest.getDescription();
        this.startDate = updateEventRequest.getStartDate();
        this.endDate = updateEventRequest.getEndDate();
        this.runtime = updateEventRequest.getRuntime();
        this.viewRating = updateEventRequest.getViewRating();
        this.rating = updateEventRequest.getRating();
        this.place = updateEventRequest.getPlace();
        this.bookAble = updateEventRequest.getBookAble();
    }
}
