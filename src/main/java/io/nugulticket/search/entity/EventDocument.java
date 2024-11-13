package io.nugulticket.search.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 엘라스틱서치에 사용되는 인덱스 (관계형 디비 table - 엘라스틱서치 index)
 * */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)  // 알 수 없는 필드 무시
@Document(indexName = "events_current")
public class EventDocument {
    @Id
    @Field(name = "event_id", type = FieldType.Long)
    private Long eventId;

    @Field(name = "category", type = FieldType.Text)
    private String category;

    @Field(name = "title", type = FieldType.Text)
    private String title;

    @Field(name = "description", type = FieldType.Text)
    private String description;

    @Field(name = "startDate", type = FieldType.Date, format = DateFormat.date)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String startDate;

    @Field(name = "endDate", type = FieldType.Date, format = DateFormat.date)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String endDate;

    @Field(name = "runtime", type = FieldType.Text)
    private String runtime;

    @Field(name = "viewRating", type = FieldType.Text)
    private String viewRating;

    @Field(name = "rating", type = FieldType.Double)
    private Double rating;

    @Field(name = "place", type = FieldType.Text)
    private String place;

    @Field(name = "bookAble", type = FieldType.Boolean)
    private Boolean bookAble;

    @Field(name = "imageUrl", type = FieldType.Text)
    private String imageUrl;

    @Field(name = "titleInitials", type = FieldType.Text)
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
