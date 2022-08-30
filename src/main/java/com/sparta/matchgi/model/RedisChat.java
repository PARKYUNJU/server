package com.sparta.matchgi.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("Chat")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RedisChat  implements Serializable {

    @Id
    @Indexed
    private String id;

    @Indexed
    private String roomId;

    private Room room;

    private User user;

    private String message;

    @Indexed
    private LocalDateTime createdAt;


    public RedisChat(String roomId,Room room, String message, User user, LocalDateTime createdAt) {
        this.roomId = roomId;
        this.room = room;
        this.message = message;
        this.user = user;
        this.createdAt = createdAt;
    }
}