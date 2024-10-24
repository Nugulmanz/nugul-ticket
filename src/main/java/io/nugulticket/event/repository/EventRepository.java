package io.nugulticket.event.repository;

import io.nugulticket.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {

    /**
     * 4가지 조건 중 해당하는 공연을 검색할 수 있는 쿼리
     * @param keyword 공연제목에 들어간 키워드
     * @param eventDate 공연 시작일-종료일 사이
     * @param place
     * @param category
     * @param pageable
     * @return Event 페이지로 리턴
     */
    @Query("SELECT e FROM Event e " +
            "WHERE (:keyword IS NULL OR e.title LIKE CONCAT('%',:keyword,'%'))" +
            "AND (:eventDate IS NULL OR :eventDate BETWEEN e.startDate AND e.endDate)" +
            "AND (:place IS NULL OR e.place = :place)" +
            "AND (:category IS NULL OR e.category = :category) " +
            "ORDER BY e.eventId, e.title")
    Page<Event> findByKeywords(String keyword, LocalDate eventDate, String place, String category, Pageable pageable);

    List<Event> findByUser_Id(Long id);
}
