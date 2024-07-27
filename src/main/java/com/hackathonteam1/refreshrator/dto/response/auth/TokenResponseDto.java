package com.hackathonteam1.refreshrator.dto.response.auth;

import com.hackathonteam1.refreshrator.entity.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDto {
    private String AccessToken;
    private RefreshToken refreshToken;
}
