package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.*;
import com.datingapp.datingapp.exception.ResourceNotFoundException;
import com.datingapp.datingapp.services.UserInterestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserInterestController {
    private UserInterestService userInterestService;
    private final List<InterestDto> interests;
    private final List<CategoryDto> categoryInterests;
    private final Map<Integer, String> interestMap;
    private final Map<Integer, String> categoryMap;


    private static final Logger log = LoggerFactory.getLogger(UserInterestController.class);

    @Autowired
    public UserInterestController(UserInterestService userInterestService, ResourceLoader loader, ObjectMapper mapper, Map<Integer, String> interestMap, Map<Integer, String> categoryMap) throws IOException {
        this.userInterestService = userInterestService;
        this.interestMap = interestMap;
        this.categoryMap = categoryMap;

        Resource resource = loader.getResource("classpath:static/data/interests.json");
        this.interests = Arrays.asList(
                mapper.readValue(resource.getInputStream(), InterestDto[].class)
        );
        for(var interest: interests){
            this.interestMap.put(interest.getPkInterest(), interest.getName());
        }
        Resource categoryInterests = loader.getResource("classpath:static/data/categories.json");
        this.categoryInterests = Arrays.asList(
                mapper.readValue(categoryInterests.getInputStream(), CategoryDto[].class)
        );
        for(var categoryInterest: this.categoryInterests){
            this.categoryMap.put(categoryInterest.getPkCategory(), categoryInterest.getName());
        }
    }

    @GetMapping("/interests")
    public List<InterestDto> listInterests() {
        return interests;
    }

    @GetMapping("/categories/{id}/interests")
    public ResponseEntity<List<InterestDto>> listInterestById(@PathVariable int id) {
        List<InterestDto> interestList = userInterestService.listInterestByIdCategory(id, interests);
        return ResponseEntity.ok(interestList);
    }

    @GetMapping("/categories")
    public List<CategoryDto> listCategories() {
        return categoryInterests;
    }

    @GetMapping("/users/{user_id}/profile_fields")
    public List<UserFieldDto> list(@PathVariable int user_id) {
        return userInterestService.getUserProfileFields(user_id);
    }

    @PostMapping("/users/{user_id}/profile_fields")
    public ResponseEntity<Void> save(
            @PathVariable int user_id,
            @RequestBody List<UserFieldDto> payload
    ) throws ResourceNotFoundException {
        userInterestService.saveUserFields(user_id, payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{user_id}/categories/{category_id}/interests")
    public ResponseEntity<List<UserInterestDto>> listUserInterestByCategory(
            @PathVariable int user_id, @PathVariable int category_id) {
        List<UserInterest> userInterests =
                userInterestService.userListInterestByIdCategory(user_id, category_id, interests);
        List<UserInterestDto> userInterestsDto = new ArrayList<>();
        for (UserInterest userInterest : userInterests) {
            Integer interestId = userInterest.getId().getInterestId();
            String name = interestMap.get(interestId);
            Integer weight = userInterest.getWeight();
            String description = userInterest.getDescription();

            UserInterestDto userInterestDto = new UserInterestDto(
                    interestId, name, weight, description);
            userInterestsDto.add(userInterestDto);
        }
        return ResponseEntity.ok(userInterestsDto);
    }
    @GetMapping("/users/{userId}/interests")
    public ResponseEntity<List<UserInterestDto>> listUserInterest(@PathVariable("userId") int userId){
        List<UserInterest> userInterests = userInterestService.userListInterest(userId);
        List<UserInterestDto> userInterestsDto = new ArrayList<>();
        for (UserInterest userInterest : userInterests) {
            Integer interestId = userInterest.getId().getInterestId();
            String name = interestMap.get(interestId);
            Integer weight = userInterest.getWeight();
            String description = userInterest.getDescription();
            UserInterestDto userInterestDto = new UserInterestDto(
                    interestId, name, weight, description);
            userInterestsDto.add(userInterestDto);
        }
        return ResponseEntity.ok(userInterestsDto);

    }


    @PostMapping("/users/{userId}/interests")
    public ResponseEntity<Void> addUserInterest(@PathVariable("userId") int userId,
                                                                 @RequestBody List<UserInterestDto> payload) {
        userInterestService.addUserInterest(userId, payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/interests/remove")
    public ResponseEntity<Void> removeUserInterest(@PathVariable("userId") int userId,
                                                @RequestBody List<UserInterestDto> payload) {
        userInterestService.removeUserInterest(userId, payload);
        return ResponseEntity.ok().build();
    }

}
