package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.Like;
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

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);
    @PostMapping
    public ResponseEntity<Integer> setLike(@RequestBody Like like) {
        int id = likeService.setLike(like);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/my_likes/{user_id}")
    public ResponseEntity<List<Object[]>> getMyLikes(@PathVariable("user_id") int userId) {
        return ResponseEntity.ok(likeService.getMyLikes(userId));
    }

    @GetMapping("/received_likes/{user_id}")
    public ResponseEntity<List<Object[]>> getReceivedLikes(@PathVariable("user_id") int userId) {
        return ResponseEntity.ok(likeService.getReceivedLikes(userId));
    }

    @DeleteMapping
    public ResponseEntity<Integer> deleteLike(
            @RequestParam int liker,
            @RequestParam int poster) {
        int deleted = likeService.deleteLike(liker, poster);
        return ResponseEntity.ok(deleted);
    }
}
