package com.hackathonteam1.refreshrator.util;

import com.hackathonteam1.refreshrator.authentication.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



@Component
public class RefreshTokenUtil extends JwtTokenProvider{
    public RefreshTokenUtil(@Value("${security.jwt.refresh-token.secret-key}") String secretKey,
                            @Value("${security.jwt.refresh-token.expire-length}") long validityInMilliseconds) {
        super(secretKey, validityInMilliseconds);
    }
}
