package com.sparta.matchgi.dto;

import com.sparta.matchgi.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationDetailResponseDto {
    private Long postId;
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private boolean isread;

    public NotificationDetailResponseDto(Notification notification) {
        this.postId = notification.getPost().getId();
        this.id = notification.getId();
        this.content = notification.getContent();
        this.createdAt = notification.getCreatedAt();
        this.isread = notification.isIsread();
    }
}
