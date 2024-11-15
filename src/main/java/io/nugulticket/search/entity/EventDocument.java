package io.nugulticket.search.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)  // 알 수 없는 필드 무시
public class EventDocument {
    private Long eventId;
    private String category;
    private String title;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String endDate;

    private String runtime;
    private String viewRating;
    private Double rating;
    private String place;
    private Boolean bookAble;
    private String imageUrl;
    private String titleInitials;  //제목 초성 필드


    public EventDocument(Long eventId, String category, String title, String description, String formattedStartDate, String formattedEndDate,
                         String runtime, String viewRating, Double rating, String place, Boolean bookAble, String imageUrl) {
        this.eventId = eventId;
        this.title= title;
        this.description = description;
        this.category = category;
        this.startDate = formattedStartDate;
        this.endDate = formattedEndDate;
        this.runtime = runtime;
        this.viewRating = viewRating;
        this.rating = rating;
        this.place = place;
        this.bookAble = bookAble;
        this.imageUrl = imageUrl;
    }
}
