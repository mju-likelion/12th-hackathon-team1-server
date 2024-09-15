package com.hackathonteam1.refreshrator.authentication;


import com.hackathonteam1.refreshrator.exception.UnauthorizedException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    //생성자
    public JwtTokenProvider(String secretKey, long validityInMilliseconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    //로그인시 토큰을 발급함
    public String createToken(final String payload) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()
                .setSubject(payload)    //userid
                .setIssuedAt(now)       //발급 시간
                .setExpiration(expiration)  //만료 시간
                .signWith(key, SignatureAlgorithm.HS256)    //서명
                .compact(); //문자열로 반환
    }

    //페이로드 분석,userId 반환
    public String getPayload(final String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key) //키 설정
                    .build()
                    .parseClaimsJws(token)//parse:분석하다, 여기서 토큰이 유효하지 않으면 JwtException이 발생
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }
    }
}