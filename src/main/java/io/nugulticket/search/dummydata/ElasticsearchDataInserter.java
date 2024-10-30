package io.nugulticket.search.dummydata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.nugulticket.event.entity.Event;
import io.nugulticket.search.dto.searchEvents.SearchEventsResponse;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 생성한 더미 데이터를 Elasticsearch 에 주입하는 Bulk API
 */
public class ElasticsearchDataInserter {
    private static final String ELASTICSEARCH_URL = "http://localhost:9200/events/_bulk";

    public static void main(String[] args) throws Exception {
        // Elasticsearch 인증 정보
        String username = System.getenv("ES_USERNAME"); // 사용자 이름
        String password = System.getenv("ES_PASSWORD"); // 비밀번호

        // 인증 정보 설정
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope("localhost", 9200),
                new UsernamePasswordCredentials(username, password)
        );

        // 인증 정보 포함하여 HTTP 클라이언트 생성
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        StringBuilder bulkRequest = new StringBuilder();

        for (int i = 0; i < 10000; i++) { // 100개의 더미 데이터 생성
            Event event = DummyEventGenerator.createDummyEvent();

            // SearchEventsResponse로 변환
            SearchEventsResponse eventResponse = new SearchEventsResponse(
                    event.getEventId(),
                    event.getCategory(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getStartDate(),
                    event.getEndDate(),
                    event.getRuntime(),
                    event.getViewRating(),
                    event.getRating(),
                    event.getPlace(),
                    event.getBookAble(),
                    event.getImageUrl()
            );
            String jsonData = objectMapper.writeValueAsString(eventResponse); // 객체를 JSON으로 변환
            System.out.println("jsonData = " + jsonData);

            // Bulk API 형식에 맞게 요청 데이터 추가
            bulkRequest.append("{ \"index\": { \"_index\": \"events\", \"_id\": \"" + event.getEventId() + "\" } }\n");
            bulkRequest.append(jsonData + "\n");
        }

        // Bulk API 요청
        HttpPost post = new HttpPost(ELASTICSEARCH_URL);
        post.setEntity(new StringEntity(bulkRequest.toString()));
        post.setHeader("Content-Type", "application/json");

        // httpClient.execute(post); // Elasticsearch에 데이터 전송
        HttpResponse response = httpClient.execute(post);
        System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response Body: " + responseBody);

        httpClient.close(); // HTTP 클라이언트 종료

    }

}
