package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event, Integer> {
    @Query(value = """
SELECT e.*
FROM event e
WHERE e.start_time >= :startTime
  AND (
    (e.end_time IS NOT NULL AND e.end_time <= :endTime) OR
    (e.end_time IS NULL     AND e.start_time <= :endTime)
  )
  AND e.capacity <= :capacity
ORDER BY e.start_time
LIMIT :limit OFFSET :offset;
""",nativeQuery = true
    )
    List<Event> queryEvents(
            @Param("startTime") Timestamp startTime,
            @Param("endTime") Timestamp endTime,
            @Param("capacity")  Integer capacity,
            @Param("limit")     Integer limit,
            @Param("offset")    Integer offset
    );

}
