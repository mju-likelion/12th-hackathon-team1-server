package com.hackathonteam1.refreshrator.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDto {
    private String AccessToken;
    private String refreshToken;
}
