package com.sparta.matchgi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReviewListResponseDto {

    private Long reviewId;

    private String nickname;

    private String title;

    private String content;

    private List<String> imgUrlList;

}
