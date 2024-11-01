package io.nugulticket.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CalenderEventResponse {
    private Map<String, List<EventSimpleResponse>> ticketCalender;

    static public CalenderEventResponse of(List<EventSimpleResponse> ticketCalender) {
        Map<String, List<EventSimpleResponse>> ticketCalenderMap = new HashMap<>();
        for (EventSimpleResponse ticketDetailResponse : ticketCalender) {
            String date = ticketDetailResponse.getBeginDate().toString();
            if (!ticketCalenderMap.containsKey(date)) {
                ticketCalenderMap.put(date, new ArrayList<>());
            } else {
                ticketCalenderMap.get(date).add(ticketDetailResponse);
            }
        }
        return new CalenderEventResponse(ticketCalenderMap);
    }
}
