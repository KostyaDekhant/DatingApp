package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.FormDTO;
import com.datingapp.datingapp.services.FormsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormsController {
    private final FormsService formsService;

    @GetMapping
    public ResponseEntity<List<FormDTO>> getForms(
            @RequestParam("userId") int userId,
            @RequestParam("age_min") int age_min,
            @RequestParam("age_max") int age_max,
            @RequestParam("height_min") int height_min,
            @RequestParam("height_max") int height_max,
            @RequestParam("gender") String gender,
            @RequestParam("limit") int limit,
            @RequestParam("offset") int offset
            ) {
        return ResponseEntity.ok(formsService.findQuestUsers(userId, age_min, age_max
                                            , height_min, height_max, limit, offset, gender));
    }
}
