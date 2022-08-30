package com.sparta.matchgi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.matchgi.model.ImgUrl;
import com.sparta.matchgi.model.MatchStatus;
import com.sparta.matchgi.model.Post;
import com.sparta.matchgi.model.SubjectEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CreatePostResponseDto {
    private String title;

    private Long postId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime matchDeadline;

    private double lat;

    private double lng;

    private String address;

    private String subject;

    private String content;

    private MatchStatus matchStatus;

    private List<ImagePathDto> imgpaths;

    private List<ImageUrlDto> imgurls;

    private int owner;

    private int viewCount;

    private int player;

    private String nickname;

    private String profileImgUrl;



}