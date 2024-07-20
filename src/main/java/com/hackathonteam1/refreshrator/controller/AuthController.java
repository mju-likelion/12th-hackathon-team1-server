package com.hackathonteam1.refreshrator.controller;

import com.hackathonteam1.refreshrator.authentication.AuthenticationExtractor;
import com.hackathonteam1.refreshrator.authentication.JwtEncoder;
import com.hackathonteam1.refreshrator.dto.ResponseDto;
import com.hackathonteam1.refreshrator.dto.request.auth.LoginDto;
import com.hackathonteam1.refreshrator.dto.request.auth.SigninDto;
import com.hackathonteam1.refreshrator.dto.response.auth.TokenResponseDto;
import com.hackathonteam1.refreshrator.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    //회원가입
    @PostMapping("/signin")
    public ResponseEntity<ResponseDto<Void>> signup(@RequestBody @Valid SigninDto signinDto) {

        authService.signin(signinDto);

        return new ResponseEntity<>(ResponseDto.res(HttpStatus.CREATED, "회원 가입 완료"), HttpStatus.CREATED);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<Void>> login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {

        TokenResponseDto tokenResponseDto = authService.login(loginDto);
        String bearerToken = JwtEncoder.encodeJwtToken(tokenResponseDto.getAccessToken());

        ResponseCookie cookie = ResponseCookie.from(AuthenticationExtractor.TOKEN_COOKIE_NAME, bearerToken)
                .maxAge(Duration.ofMillis(1800000))
                .path("/")
                .httpOnly(true)
                .sameSite("None").secure(true)
                .build();
        response.addHeader("set-cookie", cookie.toString());

        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "로그인 완료"), HttpStatus.OK);
    }
}
