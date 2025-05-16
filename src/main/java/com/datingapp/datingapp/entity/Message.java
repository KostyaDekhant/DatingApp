package com.datingapp.datingapp.entity;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private Integer pkMessage;

    @Column(name = "message")
    @JsonProperty("message")
    private String message;

    @Column(name = "time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp time;

    @Column(name = "pk_user")
    @JsonProperty("pk_user")
    private int pkUser;
    @Column(name = "pk_chat")
    @JsonProperty("pk_chat")
    private int pkChat;

    public Message(String message, Timestamp time, int pkUser, int pkChat) {
        this.message = message;
        this.time = time;
        this.pkUser = pkUser;
        this.pkChat = pkChat;
    }

    public Message() {
        this.message = "";
        this.time = new Timestamp(0);
        this.pkUser = -1;
        this.pkChat = -1;
    }

    public Message(MessageDTO messageDTO) {
        this.message = messageDTO.getMessage();
        this.time = messageDTO.getTime();
        this.pkUser = messageDTO.getPkUser();
        this.pkChat = messageDTO.getPkChat();
    }

    @Override
    public String toString() {
        return "Message{" +
                "pk_message=" + pkMessage +
                ", message='" + message + '\'' +
                ", time=" + time +
                ", pk_user=" + pkUser +
                ", pk_chat=" + pkChat +
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
                    message.setPkUser(Integer.parseInt(entry[1]));
                    break;
                case "pk_chat":
                    message.setPkChat(Integer.parseInt(entry[1]));
                    break;
            }
        }

        return message;
    }

}
