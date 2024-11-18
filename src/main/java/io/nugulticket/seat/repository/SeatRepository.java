package io.nugulticket.seat.repository;

import io.nugulticket.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat,Long> {
    List<Seat> findByEventTimeId(int eventTime_id);
}
