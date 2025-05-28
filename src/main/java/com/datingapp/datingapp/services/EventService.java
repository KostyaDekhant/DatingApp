package com.datingapp.datingapp.services;

import com.datingapp.datingapp.entity.*;
import com.datingapp.datingapp.repository.EventParticipantRepo;
import com.datingapp.datingapp.repository.EventRepo;
import com.datingapp.datingapp.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepo eventRepo;
    private final UserRepo userRepo;
    private final EventParticipantRepo eventParticipantRepo;

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    @Transactional
    public List<EventDTO> getEvents(EventParams eventParams) {
        try {
            List<EventDTO> eventDTOs = new ArrayList<>();
            List<Event> events = eventRepo.queryEvents( eventParams.getStartTime(),
                                                        eventParams.getEndTime(),
                                                        eventParams.getCapacity(),
                                                        eventParams.getLimit(),
                                                        eventParams.getOffset());
            log.info("Найденные ивенты: " + events.size());
            for (Event event : events) {
                List<Integer> membersId = eventParticipantRepo.findUserIdByEventId(event.getPkEvent());
                List<EventMemberDTO> memberDTOs = new ArrayList<>();
                for (Integer memberId : membersId) {
                    List<Object[]> memberInfo = userRepo.getUserInfoForEvent(memberId);
                    EventMemberDTO eventMemberDTO = new EventMemberDTO(memberId,
                            (String)memberInfo.getFirst()[0]);
                    memberDTOs.add(eventMemberDTO);
                }
                EventDTO eventDTO = new EventDTO(event, memberDTOs);
                eventDTOs.add(eventDTO);
            }
            log.info("Полученные ивенты: " + eventDTOs.size());
            return eventDTOs;
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка при получении мероприятий: " + e.getMessage());
        }
    }

    @Transactional
    public void createEvent(EventDTO eventDTO) {
        try{
            log.info("Сохранение ивента " + eventDTO.toString());
            Event event = new Event(eventDTO);
            event.setOrganizerId(userRepo.getUserByPkUser(eventDTO.getOrganizerId()));
            event.setChat(eventDTO.getChat() == -1 ? null : null);
            eventRepo.save(event);
            List<EventMemberDTO> memberDTOs = eventDTO.getMembers();
            for (EventMemberDTO memberDTO : memberDTOs) {
                EventParticipant eventParticipant = new EventParticipant();
                eventParticipant.setUser(userRepo.getUserByPkUser(memberDTO.getUserId()));
                eventParticipant.setEvent(event);
                eventParticipant.setJoinedAt(new Timestamp(System.currentTimeMillis()));
                eventParticipant.setRole("member");
                eventParticipantRepo.save(eventParticipant);
            }
            log.info("Успешно создан ивент");
        }catch(Exception e){
            throw new RuntimeException("Ошибка при создании мероприятия: " + e.getMessage());
        }
    }

    @Transactional
    public void addMembersToEvent(List<Integer> membersId, Integer eventId) {
        try{
            log.info("Добавление " + membersId.size() +" участников к мероприятию с id " + eventId);
            for (Integer memberId : membersId) {
                if(!eventParticipantRepo.existsByUser_idAndEvent_id(memberId, eventId)) {
                    EventParticipant eventParticipant = new EventParticipant();
                    eventParticipant.setUser(userRepo.getUserByPkUser(memberId));
                    eventParticipant.setEvent(eventRepo.findById(eventId).get());
                    eventParticipant.setJoinedAt(new Timestamp(System.currentTimeMillis()));
                    eventParticipant.setRole("member");
                    eventParticipantRepo.save(eventParticipant);
                }
                else log.info("Пользователь с id " + memberId + " уже участвует в мероприятии с id " + eventId);
            }
            log.info("Участники успешно добавлены");
        }
        catch (Exception e){
            throw new RuntimeException("Ошибка при добавлении участника к мероприятию " + e.getMessage());
        }
    }

    @Transactional
    public void removeMembersFromEvent(List<Integer> membersId, Integer eventId, Integer organizerId) {
        if(eventRepo.findById(eventId).get().getOrganizerId().getPkUser() != (organizerId)) {
            String problem = "Участник с id " + organizerId + " не имеет прав на удаление участников из мероприятия с id " + eventId;
            log.info(problem);
            throw new RuntimeException(problem);
        }
        try{
            log.info("Удаление " + membersId.size() +" участников из мероприятия с id " + eventId);
            for (Integer memberId : membersId) {
                if(eventParticipantRepo.deleteByUserIdAndEventId(memberId, eventId) != 0)
                    log.info("Удалён участник с id: " + memberId);
                else log.info("Участник с id " + memberId + " не участвовал в мероприятии с id " + eventId);
            }
            log.info("Участники успешно удалены");
        }
        catch (Exception e){
            throw new RuntimeException("Ошибка при удалении участника из мероприятию " + e.getMessage());
        }
    }
}
