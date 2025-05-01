package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.services.FormsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormsController {
    private final FormsService formsService;

    @GetMapping
    public ResponseEntity<Object[]> getForms(
            @RequestParam("user_id") int userId,
            @RequestParam("prev_user_id") int prevUserId) {
        return ResponseEntity.ok(formsService.findQuestUsers(userId, prevUserId));
    }
}
