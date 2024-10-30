package io.nugulticket.search.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;

/**
 * 엘라스틱서치에 사용되는 인덱스 (관계형 디비 table - 엘라스틱서치 index)
 * */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Document(indexName = "events")
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

    @Field(name = "startDate", type = FieldType.Date, format = DateFormat.basic_date)
    private LocalDate startDate;

    @Field(name = "endDate", type = FieldType.Date, format = DateFormat.basic_date)
    private LocalDate endDate;

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


}
