package com.hackathonteam1.refreshrator.controller;

import com.hackathonteam1.refreshrator.annotation.AuthenticatedUser;
import com.hackathonteam1.refreshrator.authentication.AuthenticationExtractor;
import com.hackathonteam1.refreshrator.dto.ResponseDto;
import com.hackathonteam1.refreshrator.dto.request.auth.LoginDto;
import com.hackathonteam1.refreshrator.dto.request.auth.SigninDto;
import com.hackathonteam1.refreshrator.dto.response.auth.CookiesResponse;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.service.AuthService;
import com.hackathonteam1.refreshrator.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    //회원가입
    @PostMapping("/signin")
    public ResponseEntity<ResponseDto<Void>> signup(@RequestBody @Valid SigninDto signinDto) {
        authService.signin(signinDto);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.CREATED, "회원 가입 완료"), HttpStatus.CREATED);
    }

    //회원 탈퇴
    @DeleteMapping("/leave")
    public ResponseEntity<ResponseDto<Void>> leave(@AuthenticatedUser User user,HttpServletResponse response) {
        authService.leave(user);
        CookiesResponse cookiesResponse = tokenService.expiredTokenCookies();

        response.addHeader("set-cookie", cookiesResponse.getAccessTokenCookie().toString());
        response.addHeader("set-cookie", cookiesResponse.getRefreshTokenCookie().toString());

        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "회원 탈퇴 완료"), HttpStatus.OK);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<Void>> login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response) {

        UUID userId = authService.login(loginDto);
        CookiesResponse cookies = tokenService.issueTokenCookies(userId);

        response.addHeader("set-cookie", cookies.getAccessTokenCookie().toString());
        response.addHeader("set-cookie", cookies.getRefreshTokenCookie().toString());

        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "로그인 완료"), HttpStatus.OK);
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<Void>> logout(@AuthenticatedUser User user, final HttpServletResponse response) {
        CookiesResponse cookiesResponse = tokenService.expiredTokenCookies();

        response.addHeader("set-cookie", cookiesResponse.getAccessTokenCookie().toString());
        response.addHeader("set-cookie", cookiesResponse.getRefreshTokenCookie().toString());

        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "로그아웃 완료"), HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<ResponseDto<Void>> refresh(HttpServletRequest request, HttpServletResponse response){

        String refreshToken = AuthenticationExtractor.extract(request, "RefreshToken");
        CookiesResponse cookiesResponse = tokenService.refresh(refreshToken);

        response.addHeader("set-cookie", cookiesResponse.getAccessTokenCookie().toString());
        response.addHeader("set-cookie", cookiesResponse.getRefreshTokenCookie().toString());
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "토큰 재발급 성공"), HttpStatus.OK);
    }

}
