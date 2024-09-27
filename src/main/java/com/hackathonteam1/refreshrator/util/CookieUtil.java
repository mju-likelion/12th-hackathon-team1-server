package com.hackathonteam1.refreshrator.util;

import com.hackathonteam1.refreshrator.exception.UnauthorizedException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import java.time.Duration;


public class CookieUtil {
    public static ResponseCookie createTokenCookie(
            String tokenName, String tokenValue, Duration maxAge, String path){
        return ResponseCookie.from(tokenName, tokenValue)
                .maxAge(maxAge)
                .path(path)
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();
    }

    public static Cookie getCookie(final HttpServletRequest request, final String cookieName){
        Cookie[] cookies = request.getCookies();

        if(cookies != null){
            for (Cookie cookie : cookies){
                if(cookie.getName().equals(cookieName)){
                    return cookie;
                }
            }
        }

        throw new UnauthorizedException(ErrorCode.COOKIE_NOT_FOUND);
    }
}
