package io.nugulticket.eventtime.entity;

import io.nugulticket.event.entity.Event;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "event_time")
public class EventTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_time_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "date", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "remaining_seat", nullable = false)
    private int remainingSeat;
}
