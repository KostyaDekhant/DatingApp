package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.Like;
import com.datingapp.datingapp.entity.LikeDTO;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.services.LikeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    private static final Logger log = LoggerFactory.getLogger(LikeController.class);
    @PostMapping
    public ResponseEntity<Integer> setLike(@RequestBody LikeDTO likeDTO) {
        int id = likeService.setLike(likeDTO);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/my_likes/{user_id}")
    public ResponseEntity<List<LikeDTO>> getMyLikes(@PathVariable("user_id") int userId) { //LikeDTO
        return ResponseEntity.ok(likeService.getMyLikes(userId));
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<List<LikeDTO>> getReceivedLikes(@PathVariable("user_id") int userId) { //LikeDTO
        return ResponseEntity.ok(likeService.getReceivedLikes(userId));
    }

    @DeleteMapping
    public ResponseEntity<Integer> deleteLike(  @RequestBody LikeDTO likeDTO ) throws UserNotExistsExceptions {
        int deleted = likeService.deleteLike(likeDTO);
        return ResponseEntity.ok(deleted);
    }
}
