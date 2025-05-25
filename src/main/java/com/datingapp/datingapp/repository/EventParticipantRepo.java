package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.ChatMemberDTO;
import com.datingapp.datingapp.entity.Event;
import com.datingapp.datingapp.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventParticipantRepo extends JpaRepository<EventParticipant, Integer> {

    @Query(value = """
SELECT EXISTS(
  SELECT 1
  FROM event_participant ep
  WHERE ep.user_id  = :user_id
    AND ep.event_id = :event_id
) AS is_participant;
""", nativeQuery = true)
    Boolean existsByUser_idAndEvent_id(Integer user_id, Integer event_id);


    @Query(value = """
select e.user_id from event_participant e where e.event_id = :pkEvent
""", nativeQuery = true)
    List<Integer> findUserIdByEventId(Integer pkEvent);
}
