package io.nugulticket.eventtime.repository;

import io.nugulticket.eventtime.entity.EventTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTimeRepository extends JpaRepository<EventTime, Long> {
}
