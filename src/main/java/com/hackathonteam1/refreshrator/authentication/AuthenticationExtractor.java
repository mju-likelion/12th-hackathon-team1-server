package com.hackathonteam1.refreshrator.authentication;

import com.hackathonteam1.refreshrator.exception.UnauthorizedException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class AuthenticationExtractor {

    public static String extract(final HttpServletRequest request, String cookieName) {
        Cookie cookies = CookieUtil.getCookie(request, cookieName);
        return JwtEncoder.decodeJwtBearerToken(cookies.getValue()); //디코딩
    }
}
