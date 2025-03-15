package com.example.datingappclient.retrofit;

import com.example.datingappclient.model.Picture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

public class ServerAPITest {
    private MockWebServer mockWebServer;
    private ServerAPI serverAPI;

    @Before
    public void setUp() throws Exception {
        // Создание и запуск MockWebServer
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // Настройка Retrofit для использования MockWebServer
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))  // Устанавливаем базовый URL MockWebServer
                .addConverterFactory(GsonConverterFactory.create(gson))  // Gson для конвертации JSON
                .build();

        // Создаем экземпляр ServerAPI
        serverAPI = retrofit.create(ServerAPI.class);
    }

    @After
    public void tearDown() throws Exception {
        // Останавливаем MockWebServer после теста
        mockWebServer.shutdown();
    }

    @Test
    public void testGetUser() throws Exception {
        // Мокируем ответ от сервера
        JsonObject mockResponse = new JsonObject();
        mockResponse.addProperty("id", 1);
        mockResponse.addProperty("name", "John Doe");

        mockWebServer.enqueue(new MockResponse().setBody(mockResponse.toString()).setResponseCode(200));

        // Создаем запрос
        Call<JsonObject> call = serverAPI.getUser(1);

        // Выполняем запрос и проверяем ответ
        JsonObject response = call.execute().body();

        assertNotNull(response);
        assertEquals(1, response.get("id").getAsInt());
        assertEquals("John Doe", response.get("name").getAsString());
    }

    @Test
    public void testGetUserNotFound() throws Exception {
        // Мокируем ответ от сервера с ошибкой 404
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        // Создаем запрос
        Call<JsonObject> call = serverAPI.getUser(999);

        // Выполняем запрос
        Response<JsonObject> response = call.execute();

        // Проверяем, что код ошибки 404
        assertEquals(404, response.code());

        // Проверяем, что тело ответа пустое (так как сервер вернул ошибку)
        assertNull(response.body());
    }

    @Test
    public void testGetUserServerError() throws Exception {
        // Мокируем ответ от сервера с ошибкой 500
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));

        // Создаем запрос
        Call<JsonObject> call = serverAPI.getUser(1);

        // Выполняем запрос
        Response<JsonObject> response = call.execute();

        // Проверяем, что код ошибки 500
        assertEquals(500, response.code());
        assertNull(response.body());  // Тело ответа должно быть null, так как сервер вернул ошибку
    }

    @Test
    public void testUpdateUser() throws Exception {
        // Мокируем успешный ответ от сервера
        mockWebServer.enqueue(new MockResponse().setBody("true").setResponseCode(200));

        JsonObject updateJson = new JsonObject();
        updateJson.addProperty("name", "Jane Doe");

        // Создаем запрос
        Call<Boolean> call = serverAPI.updateUser(updateJson);

        // Выполняем запрос и проверяем ответ
        Boolean response = call.execute().body();

        assertNotNull(response);
        assertTrue(response);
    }

    @Test
    public void testUpdateUserInvalidData() throws Exception {
        // Мокируем ответ от сервера с ошибкой 400 для некорректных данных
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("{\"error\":\"Invalid data\"}"));

        // Создаем некорректные данные
        JsonObject updateJson = new JsonObject();
        updateJson.addProperty("name", ""); // например, пустое имя

        // Создаем запрос
        Call<Boolean> call = serverAPI.updateUser(updateJson);

        // Выполняем запрос и проверяем, что код ошибки 400
        Response<Boolean> response = call.execute();

        // Проверяем, что код ошибки - 400 (неправильные данные)
        assertEquals(400, response.code());

        // Проверяем, что тело ответа содержит ошибку
        assertNotNull(response.errorBody());  // Проверка, что есть тело с ошибкой
        assertTrue(response.errorBody().string().contains("Invalid data"));
    }

    @Test
    public void testUploadImage() throws Exception {
        // Мокируем успешный ответ от сервера
        mockWebServer.enqueue(new MockResponse().setBody("1").setResponseCode(200));

        int userId = 1, imageId = 101;
        byte[] imageData = new byte[]{1, 2, 3, 4};
        Picture picture = new Picture(imageId, imageData, userId);

        // Создаем запрос
        Call<Integer> call = serverAPI.uploadImage(picture);

        // Выполняем запрос и проверяем ответ
        Integer response = call.execute().body();

        assertNotNull(response);
        assertEquals(Integer.valueOf(1), response);
    }

    @Test
    public void testSendLike() throws Exception {
        // Мокируем успешный ответ от сервера
        mockWebServer.enqueue(new MockResponse().setBody("1").setResponseCode(200));

        JsonObject likeJson = new JsonObject();
        likeJson.addProperty("liker", 1);
        likeJson.addProperty("poster", 2);

        // Создаем запрос
        Call<Integer> call = serverAPI.sendLike(likeJson);

        // Выполняем запрос и проверяем ответ
        Integer response = call.execute().body();

        assertNotNull(response);
        assertEquals(Integer.valueOf(1), response);
    }

    @Test
    public void testSendLikeServerError() throws Exception {
        // Настроим MockWebServer для ответа 500 Internal Server Error
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));

        // Подготавливаем данные для отправки
        JsonObject likeJson = new JsonObject();
        likeJson.addProperty("liker", 1);
        likeJson.addProperty("poster", 2);

        // Создаем запрос
        Call<Integer> call = serverAPI.sendLike(likeJson);

        // Выполняем запрос
        Response<Integer> response = call.execute();

        // Проверяем, что код ответа 500
        assertFalse(response.isSuccessful()); // Ответ не успешен
        assertNull(response.body());          // Тело ответа должно быть null, так как сервер вернул ошибку
        assertEquals(500, response.code());   // Проверяем, что код ошибки действительно 500
    }

    @Test
    public void testDeleteLike() throws Exception {
        // Мокируем успешный ответ от сервера
        mockWebServer.enqueue(new MockResponse().setBody("1").setResponseCode(200));

        // Создаем запрос
        Call<Integer> call = serverAPI.deleteLike(1, 2);

        // Выполняем запрос и проверяем ответ
        Integer response = call.execute().body();

        assertNotNull(response);
        assertEquals(Integer.valueOf(1), response);
    }

    @Test
    public void testDeleteLikeNotFound() throws Exception {
        // Мокируем ответ от сервера с ошибкой 404
        mockWebServer.enqueue(new MockResponse().setResponseCode(404).setBody("Like not found"));

        // Создаем запрос
        Call<Integer> call = serverAPI.deleteLike(1, 2);

        // Выполняем запрос
        Response<Integer> response = call.execute();

        // Проверяем, что код ошибки 404
        assertEquals(404, response.code());
        assertNull(response.body());  // Тело ответа должно быть пустым, так как лайк не найден
    }

    @Test
    public void testDeleteImage() throws Exception {
        // Мокируем успешный ответ от сервера
        mockWebServer.enqueue(new MockResponse().setBody("1").setResponseCode(200));

        // Создаем запрос для удаления изображения
        Call<Integer> call = serverAPI.deleteImage(101);

        // Выполняем запрос и проверяем ответ
        Integer response = call.execute().body();

        assertNotNull(response);
        assertEquals(Integer.valueOf(1), response);
    }

    @Test
    public void testDeleteImageServerError() throws Exception {
        // Мокируем ответ от сервера с ошибкой 500
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));

        // Создаем запрос
        Call<Integer> call = serverAPI.deleteImage(999);

        // Выполняем запрос
        Response<Integer> response = call.execute();

        // Проверяем, что код ошибки 500
        assertEquals(500, response.code());
        assertNull(response.body());  // Тело ответа должно быть null, так как сервер вернул ошибку
    }
}
