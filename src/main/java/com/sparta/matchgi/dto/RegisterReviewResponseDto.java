package com.sparta.matchgi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RegisterReviewResponseDto {

    private Long reviewId;

    private String title;

    private String content;

    private String nickname;
}
