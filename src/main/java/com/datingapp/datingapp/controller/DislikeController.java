package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.services.DislikeService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dislikes")
@RequiredArgsConstructor
public class DislikeController {
    private final DislikeService dislikeService;

    @PostMapping
    public ResponseEntity<Void> setDislike(@RequestParam("disliker") int disliker,
                                           @RequestParam("poster") int poster){

        dislikeService.setDislike(disliker, poster);
        return ResponseEntity.ok().build();
    }

}
