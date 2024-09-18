package com.hackathonteam1.refreshrator.util;

import com.hackathonteam1.refreshrator.authentication.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class AccessTokenUtil extends JwtTokenProvider {
    public AccessTokenUtil(@Value("${security.jwt.token.secret-key}") final String secretKey,
                           @Value("${security.jwt.token.expire-length}") final long validityInMilliseconds) {
        super(secretKey, validityInMilliseconds);
    }
}
