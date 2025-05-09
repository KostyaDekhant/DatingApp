package com.datingapp.datingapp.services;

import com.datingapp.datingapp.controller.UserController;
import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.entity.UserCompanyInfo;
import com.datingapp.datingapp.entity.UserCompanyInfoDto;
import com.datingapp.datingapp.entity.UserDTO;
import com.datingapp.datingapp.exception.UserAlreadyExistsExceptions;
import com.datingapp.datingapp.exception.UserExceptionsWithCode;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.repository.UserCompanyRepo;
import com.datingapp.datingapp.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordService passwordService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserCompanyRepo userCompanyRepo;

    @Transactional
    public Integer signupUser(User user) {
        Optional<User> tempOptional = userRepo.findByLogin(user.getLogin());
        if (tempOptional.isPresent()) {
            log.error("Пользователь с таким логином уже существует!");
            throw new UserAlreadyExistsExceptions("Пользователь с таким логином уже существует!");
        }
        String password = user.getPassword();
        String hashedPassword = passwordService.hashPassword(password);
        user.setPassword(hashedPassword);
        addUser(user);
        Integer id = userRepo.findByLogin(user.getLogin()).get().getPkUser();
        return id;

    }

    @Transactional
    public Integer addUser(User user) {
        Optional<User> temp = userRepo.findByLogin(user.getLogin());
        if(temp.isPresent()) {
            log.error("Пользователь с таким логином уже существует!");
            throw new UserAlreadyExistsExceptions("Пользователь с таким логином уже существует!");
        }
        User savedUser = userRepo.save(user);
        log.debug("Новый пользователь: {}", savedUser);
        return savedUser.getPkUser();
    }

    @Transactional
    public Integer loginUser(User user) throws UserExceptionsWithCode {
        Optional<User> tempOptional = userRepo.findByLogin(user.getLogin());
        if (!tempOptional.isPresent())
            throw new UserExceptionsWithCode("Пользователя с таким логином не существует!", -2);
        User temp = tempOptional.get();
        if(passwordService.verifyPassword(user.getPassword(),temp.getPassword())) {
            log.info("Пользователь успешно вошёл в систему!");
            return temp.getPkUser();
        }
        else {
            throw new UserExceptionsWithCode("Пользователь ввёл пароль неверно!", -1);
        }
    }

    @Transactional
    public Integer updateUser(UserDTO user) throws UserNotExistsExceptions {
        log.info("DTO: " +user);
        Integer id = user.getId();
        Optional<User> userOptional = userRepo.findById(id);
        if(!userOptional.isPresent()){
            throw new UserNotExistsExceptions("Данные не обновлены, так как нет пользователя с таким id!", -1);
        }
        User existingUser = userOptional.get();
        User newUser = new User(user);
        userRepo.save(updateData(existingUser, newUser));
        log.info("Данные обновлены!");
        return id;
    }

    private User updateData(User oldU, User newU){
        if(!newU.getName().equals(oldU.getName()) && !newU.getName().equals(""))
            oldU.setName(newU.getName());
        if(!newU.getBirthday().equals(oldU.getBirthday()) && !newU.getBirthday().equals(""))
            oldU.setBirthday(newU.getBirthday());
        if(!newU.getGender().equals(oldU.getGender()) && !newU.getGender().equals(""))
            oldU.setGender(newU.getGender());
        if(newU.getHeight() != oldU.getHeight() && newU.getHeight() != 0)
            oldU.setHeight(newU.getHeight());
        if(newU.getIsOnline() != oldU.getIsOnline())
            oldU.setIsOnline(newU.getIsOnline());
        if(newU.getLastOnline() != oldU.getLastOnline() && !newU.getLastOnline().equals(""))
            oldU.setLastOnline(newU.getLastOnline());
        if(!newU.getDescription().equals(oldU.getDescription()) && !newU.getDescription().equals(""))
            oldU.setDescription(newU.getDescription());
        return oldU;
    }

    @Transactional
    public UserDTO getUser(int id) throws UserNotExistsExceptions {
        Optional<User> temp = userRepo.findById(id);
        if (!temp.isPresent()){
            throw new UserNotExistsExceptions("Нет пользователя с таким id!");
        }
        UserDTO userDTO = new UserDTO(temp.get());
        log.info("Информация о пользователе: {}", userDTO);
        return userDTO;
    }

    @Transactional
    public void deleteUser(int id) throws UserNotExistsExceptions {
        if(!userRepo.existsById(id)){
            throw new UserNotExistsExceptions("Нет пользователя с таким id!");
        }
        log.info("Удалён пользователь с id: " + id);
        userRepo.deleteById(id);
    }

    @Transactional
    public UserCompanyInfoDto getUserCompanyInfo(int id) throws RuntimeException {
        try{
            Optional<UserCompanyInfo> userCompanyInfo = userCompanyRepo.findByPkUser(id);
            if(!userCompanyInfo.isPresent())
                return null;
            return new UserCompanyInfoDto(userCompanyInfo.get());
        }
        catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException("Непредвиденная ошибка: " + e.getMessage());
        }
    }

    @Transactional
    public void setUserCompanyInfo(int id, UserCompanyInfoDto ucfDto) throws RuntimeException {
        try{
            Integer ucfId = userCompanyRepo.getPkUserCompanyInfoByPkUser(id);
            UserCompanyInfo ucf = new UserCompanyInfo(ucfId, id, ucfDto.getDolzh(),
                    ucfDto.getCompanyName(), ucfDto.getOtdel(), ucfDto.getOffice());
            userCompanyRepo.save(ucf);
        }
        catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException("Непредвиденная ошибка: " + e.getMessage());
        }
    }

}
