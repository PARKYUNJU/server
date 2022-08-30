package com.sparta.matchgi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.matchgi.model.MatchStatus;
import com.sparta.matchgi.model.Post;
import com.sparta.matchgi.model.SubjectEnum;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PostFilterDto {


    private Long postId;
    private String title;
    private String subject;
    private int viewCount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime matchDeadline;
    private int requestCount;

    private LocalDateTime createdAt;

    private MatchStatus matchStatus;

    private String imgUrl;
    //private String distance;
    private double lat;
    private double lng;

    private String address;



}
