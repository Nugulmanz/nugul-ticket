package io.nugulticket.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventDetails {
    private String eventId;
    private String title;
    private String category;
    private String place;

}
