package io.nugulticket.event.repository;

import io.nugulticket.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface EventRepository extends JpaRepository<Event,Long> {
    /**
     * 시작 ~ 끝 일 사이에 공연하는 모든 목록을 반환하는 쿼리 메서드
     * @param startDate 조회 시작 일시
     * @param endDate 조회 끝 일시
     * @return 시작 ~ 끝 일 사이에 한 번이라도 공연 하는 모든 이벤트
     */
    @Query("SELECT e FROM Event e " +
            "WHERE (e.startDate BETWEEN :startDate AND :endDate) " +
            "OR (e.endDate BETWEEN :startDate And :endDate)")
    List<Event> findByBetweenTwoDate(
            LocalDate startDate, LocalDate endDate);
    List<Event> findByUser_Id(Long id);
}
