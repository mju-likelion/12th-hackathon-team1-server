package com.hackathonteam1.refreshrator.dto.response.auth;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@AllArgsConstructor
@Getter
public class CookiesResponse {
    private final ResponseCookie accessTokenCookie;
    private final ResponseCookie refreshTokenCookie;
}
