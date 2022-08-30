package com.sparta.matchgi.dto;

import com.sparta.matchgi.model.SubjectEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MyPostDetailResponseDto {
    private Long id;
    private String title;
    private SubjectEnum subject;
    private List<String> imageUrl;
    private LocalDateTime createdAt;
}





