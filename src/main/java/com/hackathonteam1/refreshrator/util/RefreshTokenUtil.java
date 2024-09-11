package com.hackathonteam1.refreshrator.util;

import com.hackathonteam1.refreshrator.authentication.JwtTokenProvider;
import com.hackathonteam1.refreshrator.exception.NotFoundException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Component
public class RefreshTokenUtil extends JwtTokenProvider{

    private final static int TIMEOUT = 14;
    private final static TimeUnit TIME_UNIT = TimeUnit.DAYS;
    private RedisUtil<String, String> redisUtilForRefreshToken;

    public RefreshTokenUtil(@Value("${security.jwt.refresh-token.secret-key}") String secretKey,
                            @Value("${security.jwt.refresh-token.expire-length}") long validityInMilliseconds,
                            RedisUtil<String, String > redisUtil) {
        super(secretKey, validityInMilliseconds);
        this.redisUtilForRefreshToken = redisUtil;
    }

    @Override
    public String createToken(String payload) {
        String refreshToken = super.createToken(payload);
        redisUtilForRefreshToken.save(payload, refreshToken, TIMEOUT, TIME_UNIT); //redis의 set메서드로, Id가 동일할 경우 기존 value가 덮어씌워짐.
        return refreshToken;
    }

    public boolean isValidToken(String refreshToken, String tokenId){
        Optional<String> optionalExistToken = redisUtilForRefreshToken.findByKey(tokenId);
        String existToken = optionalExistToken.orElseThrow(
                ()-> new NotFoundException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)); //쿠키에 리프레쉬 토큰이 존재했으나 redis에는 존재하지 않을 때
        if(existToken.equals(refreshToken)){ //기존 redis에 저장되어있던 refreshToken과 일치하는지 확인.
            return true;
        }
        return false;
    }

}
