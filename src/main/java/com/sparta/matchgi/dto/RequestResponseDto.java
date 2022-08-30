package com.sparta.matchgi.dto;

import com.sparta.matchgi.model.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class RequestResponseDto {

    private Long requestId;

    private String nickname;

    private RequestStatus status;

    private String profileImgUrl;

}
