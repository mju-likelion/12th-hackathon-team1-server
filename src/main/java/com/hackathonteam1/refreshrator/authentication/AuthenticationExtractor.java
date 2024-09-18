package com.hackathonteam1.refreshrator.authentication;

import com.hackathonteam1.refreshrator.exception.UnauthorizedException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class AuthenticationExtractor {

    public static String extract(final HttpServletRequest request, String cookieName) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return JwtEncoder.decodeJwtBearerToken(cookie.getValue()); //디코딩
                }
            }
        }
        throw new UnauthorizedException(ErrorCode.COOKIE_NOT_FOUND);
    }
}
