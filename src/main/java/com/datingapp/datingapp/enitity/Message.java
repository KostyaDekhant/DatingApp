package com.datingapp.datingapp.enitity;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Getter
@Setter
@Entity
@Table(name = "\"message\"")

public class Message {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_message")
    @JsonProperty("pk_message")
    private int pk_message;

    @Column(name = "message")
    @JsonProperty("message")
    private String message;

    @Column(name = "time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp time;

    @Column(name = "pk_user")
    @JsonProperty("pk_user")
    private int pk_user;
    @Column(name = "pk_chat")
    @JsonProperty("pk_chat")
    private int pk_chat;

    public Message(String message, Timestamp time, int pk_user, int pk_chat) {
        this.message = message;
        this.time = time;
        this.pk_user = pk_user;
        this.pk_chat = pk_chat;
    }

    public Message() {
        this.pk_message = -1;
        this.message = "";
        this.time = new Timestamp(0);
        this.pk_user = -1;
        this.pk_chat = -1;
    }

    @Override
    public String toString() {
        return "Message{" +
                "pk_message=" + pk_message +
                ", message='" + message + '\'' +
                ", time=" + time +
                ", pk_user=" + pk_user +
                ", pk_chat=" + pk_chat +
                '}';
    }

    public static Message fromString(String str) {
        Message message = new Message();

        // Удаление начальной и конечной скобки
        str = str.substring(str.indexOf('{') + 1, str.lastIndexOf('}'));

        // Разделение строки на пары "ключ=значение"
        String[] keyValuePairs = str.split(", ");

        for (String pair : keyValuePairs) {
            String[] entry = pair.split("=");
            switch (entry[0]) {
                case "message":
                    message.setMessage(entry[1].replaceAll("'", "")); // Удаление кавычек
                    break;
                case "time":
                    if ("time".equals(entry[0])) {
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(entry[1].replaceAll("'", ""),
                                DateTimeFormatter.ISO_DATE_TIME);
                        Timestamp timestamp = Timestamp.from(zonedDateTime.toInstant());
                        message.setTime(timestamp);
                    }
                    break;
                case "pk_user":
                    message.setPk_user(Integer.parseInt(entry[1]));
                    break;
                case "pk_chat":
                    message.setPk_chat(Integer.parseInt(entry[1]));
                    break;
            }
        }

        return message;
    }

}
