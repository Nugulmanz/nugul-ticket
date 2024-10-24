package io.nugulticket.event.repository;

import io.nugulticket.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findByUser_Id(Long id);
}
