package com.sparta.matchgi.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RefreshResponseDto {

    private String accessToken;

    private String refreshToken;
}
