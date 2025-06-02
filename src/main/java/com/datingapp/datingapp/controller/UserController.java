package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.entity.UserCompanyInfo;
import com.datingapp.datingapp.entity.UserCompanyInfoDto;
import com.datingapp.datingapp.entity.UserDTO;
import com.datingapp.datingapp.exception.UserCompanyInfoNotExistsException;
import com.datingapp.datingapp.exception.UserExceptionsWithCode;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.services.OnlineStatusService;
import com.datingapp.datingapp.services.PasswordService;
import com.datingapp.datingapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    //Добавление пользователя
    @PostMapping("/users")
    public ResponseEntity<Integer> addUser(@Validated @RequestBody User user) {
        Integer id = userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    //Получение данных о пользователе
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) throws UserNotExistsExceptions {
        UserDTO userDTO = userService.getUser(id);
        return ResponseEntity.ok(userDTO);
    }

    //Обновление данных пользователя
    @PatchMapping("/users/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable("userId") Integer userId,
                                           @RequestBody UserDTO user) throws UserNotExistsExceptions {
        Integer id = userService.updateUser(user);
        return ResponseEntity.ok().build();
    }

    //Регистрация
    @PostMapping("/signup")
    public ResponseEntity<Integer> signupUser(@RequestBody User user) {
        Integer id = userService.signupUser(user);
        return ResponseEntity.ok(id);
    }

    //Авторизация
    @PostMapping("/login")
    public ResponseEntity<Integer>  loginUser(@RequestBody User user) throws UserExceptionsWithCode {
        Integer id = userService.loginUser(user);
        return ResponseEntity.ok(id);
    }

    //Удаление пользователей по id
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) throws UserNotExistsExceptions {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{id}/company_info")
    public ResponseEntity<UserCompanyInfoDto> getUserCompanyInfo(@PathVariable int id) throws UserNotExistsExceptions,
            UserCompanyInfoNotExistsException {
        UserCompanyInfoDto userCompanyInfo = userService.getUserCompanyInfo(id);
        return ResponseEntity.ok(userCompanyInfo);
    }

    @PatchMapping("/users/{id}/company_info")
    public ResponseEntity<Void> patchUserCompanyInfo(@PathVariable int id, @RequestBody UserCompanyInfoDto ucfDto)
            throws RuntimeException {
        //userService.setUserCompanyInfo(id, ucfDto);
        userService.upsertUserCompanyInfo(id, ucfDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/online")
    public List<OnlineStatusService.OnlineStatus> getOnlineUsers() {
        List<OnlineStatusService.OnlineStatus> onlineUsers = new ArrayList<>();
        List<Integer> onlineList = userService.findWhoIsOnline();
        for (Integer i : onlineList) {
            onlineUsers.add(new OnlineStatusService.OnlineStatus(i, true, null));
        }
        List<Integer> offlineList = userService.findWhoIsOffline();
        for (Integer i : offlineList) {
            onlineUsers.add(new OnlineStatusService.OnlineStatus(i, false, userService.getUserById(i).getLastOnline()));
        }
        return onlineUsers;
    }

}
