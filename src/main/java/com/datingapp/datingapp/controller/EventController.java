package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.EventDTO;
import com.datingapp.datingapp.entity.EventParams;
import com.datingapp.datingapp.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<List<EventDTO>> getEvents(@RequestBody EventParams eventParams){
        return ResponseEntity.ok(eventService.getEvents(eventParams));
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createEvent(@RequestBody EventDTO eventDTO){
        eventService.createEvent(eventDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{eventId}/members")
    public ResponseEntity<Void> addMembersToEvent(@RequestParam("memberIds") List<Integer> memberIds,
                                                  @PathVariable("eventId") Integer eventId){
        eventService.addMembersToEvent(memberIds, eventId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}/members")
    public ResponseEntity<Void> removeMembersToEvent(@RequestParam("memberIds") List<Integer> memberIds,
                                                     @PathVariable("eventId") Integer eventId,
                                                     @RequestParam("organizerId") Integer organizerId){
        eventService.removeMembersFromEvent(memberIds, eventId, organizerId);
        return ResponseEntity.ok().build();
    }
}
