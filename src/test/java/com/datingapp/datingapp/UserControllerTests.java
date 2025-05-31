package com.datingapp.datingapp;

import com.datingapp.datingapp.controller.UserController;
import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.repository.UserRepo;
import com.datingapp.datingapp.services.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTests {

    private MockMvc mockMvc;

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UserController userController;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }


    // Проверка успешного создания пользователя и возвращения статуса 201 (Created)
    @Test
    public void testCreateUser_Success() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse("01-01-2003", formatter);
        Timestamp timestamp = Timestamp.from(Instant.now());
        String hashedPassword = passwordService.hashPassword("Hash1023");
        String salt = passwordService.extractSalt(hashedPassword);

        // Создание тестового пользователя
        User user = new User("testUser", date, 180, "Male", true, timestamp,
                hashedPassword, "Test description", "testLogin", salt, null);

        // Мокинг поведения репозитория при сохранении пользователя
        when(userRepo.save(any(User.class))).thenReturn(user);

        // Отправка POST-запроса на создание пользователя
        mockMvc.perform(post("/api/users")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content("{\"name\":\"testUser\",\"age\":25,\"gender\":\"Male\",\"height\":180,\"is_online\":true,\"last_online\":\"2023-01-01\",\"description\":\"Test description\"}"))
                 // Проверка статуса ответа и содержимого JSON
                 .andExpect(status().isCreated())
                 .andExpect(jsonPath("$.name").value("testUser"));
    }

    // Проверка обработки ошибки 400 (Bad Request) при передаче некорректных данных для создания пользователя
    @Test
    public void testCreateUser_BadRequest() throws Exception {
        // Отправка POST-запроса с некорректными данными для создания пользователя
        mockMvc.perform(post("/api/users")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content("{\"name\":\"\",\"age\":-1,\"gender\":\"\",\"height\":-1,\"is_online\":true,\"last_online\":\"2023-01-01\",\"description\":\"\"}"))
                 // Проверка статуса ответа
                 .andExpect(status().isBadRequest());
    }

    // Проверка корректности получения данных пользователя по ID при успешном ответе (200)
    @Test
    public void testGetUser_ById_Success() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse("01-01-2003", formatter);
        Timestamp timestamp = Timestamp.from(Instant.now());
        String hashedPassword = passwordService.hashPassword("Hash1023");
        String salt = passwordService.extractSalt(hashedPassword);

        // Создание тестового пользователя
        User user = new User("testUser", date, 180, "Male", true, timestamp,
                hashedPassword, "Test description", "testLogin", salt, null);
        user.setPkUser(1);

        // Мокинг поведения репозитория при поиске пользователя по ID
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        // Отправка GET-запроса на получение пользователя по ID
        mockMvc.perform(get("/api/users/1"))
                // Проверка статуса ответа и содержимого JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testUser"));
    }

    // Проверка обработки ошибки 404, если пользователь с указанным ID не найден
    @Test
    public void testGetUser_ById_NotFound() throws Exception {
        // Мокинг поведения репозитория при поиске пользователя по ID (возвращается пустой Optional)
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Отправка GET-запроса на получение пользователя по ID
        mockMvc.perform(get("/api/users/1"))
                // Проверка статуса ответа
                .andExpect(status().isNotFound());
    }

    // Проверка успешного обновления данных пользователя и возвращения статуса 200 (OK)
    @Test
    public void testUpdateUser_Success() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse("01-01-2003", formatter);
        Timestamp timestamp = Timestamp.from(Instant.now());
        String hashedPassword = passwordService.hashPassword("Hash1023");
        String salt = passwordService.extractSalt(hashedPassword);

        // Создание тестового пользователя
        User existingUser = new User("testUser", date, 180, "Male", true, timestamp,
                hashedPassword, "Test description", "testLogin", salt, null);
        existingUser.setPkUser(1);

        // Создание обновленного тестового пользователя
        User updatedUser = new User("updatedUser", date, 180, "Male", true, timestamp,
                hashedPassword, "Updated description", "testLogin", salt, null);

        // Мокинг поведения репозитория при поиске пользователя по ID и его сохранении
        when(userRepo.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepo.save(any(User.class))).thenReturn(updatedUser);

        // Отправка PATCH-запроса на обновление пользователя
        mockMvc.perform(patch("/api/users/1")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content("{\"name\":\"updatedUser\",\"age\":25,\"gender\":\"Male\",\"height\":180,\"is_online\":true,\"last_online\":\"2023-01-01\",\"description\":\"Updated description\"}"))
                 // Проверка статуса ответа и содержимого JSON
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.name").value("updatedUser"));
    }

    // Проверка обработки ошибки 404 при попытке обновления несуществующего пользователя
    @Test
    public void testUpdateUser_NotFound() throws Exception {
        // Мокинг поведения репозитория при поиске пользователя по ID (возвращается пустой Optional)
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Отправка PATCH-запроса на обновление пользователя с несуществующим ID
        mockMvc.perform(patch("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"nonExistentUser\",\"age\":25,\"gender\":\"Male\",\"height\":180,\"is_online\":true,\"last_online\":\"2023-01-01\",\"description\":\"Updated description\"}"))
                // Проверка статуса ответа
                .andExpect(status().isNotFound());
    }

    // Проверка успешного удаления пользователя и возвращения статуса 204 (No Content)
    @Test
    public void testDeleteUser_Success() throws Exception {
        // Мокинг поведения репозитория при проверке существования пользователя по ID (возвращается true)
        when(userRepo.existsById(1)).thenReturn(true);

        // Отправка DELETE-запроса на удаление пользователя
        mockMvc.perform(delete("/api/users/1"))
                // Проверка статуса ответа
                .andExpect(status().isNoContent());
    }

    // Проверка обработки ошибки 404 при попытке удаления несуществующего пользователя
    @Test
    public void testDeleteUser_NotFound() throws Exception {
        // Мокинг поведения репозитория при проверке существования пользователя по ID (возвращается false)
        when(userRepo.existsById(1)).thenReturn(false);

        // Отправка DELETE-запроса на удаление пользователя с несуществующим ID
        mockMvc.perform(delete("/api/users/1"))
                // Проверка статуса ответа
                .andExpect(status().isNotFound());
    }

    // Проверка успешного входа пользователя и возвращения статуса 200 (OK) с ID пользователя
    @Test
    public void testUser_Login_Success() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse("01-01-2003", formatter);
        Timestamp timestamp = Timestamp.from(Instant.now());
        String hashedPassword = passwordService.hashPassword("Hash1023");
        String salt = passwordService.extractSalt(hashedPassword);

        // Создание тестового пользователя
        User user = new User("testUser", date, 180, "Male", true, timestamp,
                hashedPassword, "Test description", "testLogin", salt, null);
        user.setPkUser(1);

        // Мокинг поведения репозитория при поиске пользователя по логину и сервиса верификации пароля
        when(userRepo.findByLogin("testLogin")).thenReturn(Optional.of(user));
        when(passwordService.verifyPassword("Hash1023", hashedPassword)).thenReturn(true);

        // Отправка POST-запроса на вход пользователя
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"testLogin\",\"password\":\"Hash1023\"}"))
                // Проверка статуса ответа и содержимого ответа
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    // Проверка обработки ошибки 401 (Unauthorized) при попытке входа с неправильными данными
    @Test
    public void testUser_Login_Failed() throws Exception {
        // Мокинг поведения репозитория при поиске пользователя по логину (возвращается пустой Optional)
        when(userRepo.findByLogin("wrongLogin")).thenReturn(Optional.empty());

        // Отправка POST-запроса на вход пользователя с неправильными данными
        mockMvc.perform(post("/api/login")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content("{\"login\":\"wrongLogin\",\"password\":\"wrongPassword\"}"))
                 // Проверка статуса ответа
                 .andExpect(status().isUnauthorized());
    }

}