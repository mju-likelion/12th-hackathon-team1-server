package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.authentication.JwtEncoder;
import com.hackathonteam1.refreshrator.dto.response.auth.CookiesResponse;
import com.hackathonteam1.refreshrator.exception.UnauthorizedException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.util.AccessTokenUtil;
import com.hackathonteam1.refreshrator.util.RefreshTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TokenService {
    private final RefreshTokenUtil refreshTokenUtil;
    private final AccessTokenUtil accessTokenUtil;

    private static String ACCESS_TOKEN_NAME = "AccessToken";
    private static String REFRESH_TOKEN_NAME = "RefreshToken";

    //토큰 발급
    public CookiesResponse issueTokenCookies(UUID userId){

        ResponseCookie accessTokenCookie = createAccessTokenCookie(userId);
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(userId);

        return new CookiesResponse(accessTokenCookie, refreshTokenCookie);
    }

    //refreshToken을 사용하여 토큰 재발급
    public CookiesResponse refresh(String oldRefreshToken){
        UUID userId = UUID.fromString(refreshTokenUtil.getPayload(oldRefreshToken));

        if(refreshTokenUtil.isValidToken(oldRefreshToken, userId.toString())){ //request에 존재하는 RefreshToken이 유효한지 확인.
            return issueTokenCookies(userId);
        }
        throw new UnauthorizedException(ErrorCode.INVALID_TOKEN, "유효하지 않은 RefreshToken입니다.");
    }

    //토큰 만료를 위함
    public CookiesResponse expiredTokenCookies(){
        ResponseCookie accessTokenCookie = expireAccessTokenCookie();
        ResponseCookie refreshTokenCookie = expireRefreshTokenCookie();
        return new CookiesResponse(accessTokenCookie, refreshTokenCookie);
    }

    //토큰 생성 후 반환
    private ResponseCookie createAccessTokenCookie(UUID userId){
        String payload = userId.toString();
        String bearerAccessToken = JwtEncoder.encodeJwtToken(accessTokenUtil.createToken(payload)); //JWT 토큰 생성 후 Bearer 처리

        ResponseCookie responseCookie = ResponseCookie.from(ACCESS_TOKEN_NAME, bearerAccessToken)
                .maxAge(Duration.ofMillis(1800000))
                .path("/")
                .httpOnly(true)
                .sameSite("None").secure(true)
                .build();
        return responseCookie;
    }

    private ResponseCookie createRefreshTokenCookie(UUID userId){
        String payload = userId.toString();
        String bearerRefreshToken = JwtEncoder.encodeJwtToken(refreshTokenUtil.createToken(payload));

        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN_NAME, bearerRefreshToken)
                .maxAge(Duration.ofDays(14))
                .path("/auth/refresh")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();
        return responseCookie;
    }

    private ResponseCookie expireAccessTokenCookie(){
        ResponseCookie responseCookie = ResponseCookie.from(ACCESS_TOKEN_NAME, null)
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .sameSite("None").secure(true)
                .build();
        return responseCookie;
    }

    private ResponseCookie expireRefreshTokenCookie(){
        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN_NAME, null)
                .maxAge(0)
                .path("/auth/refresh")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();
        return responseCookie;
    }

}
