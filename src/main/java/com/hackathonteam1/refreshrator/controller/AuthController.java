package com.hackathonteam1.refreshrator.controller;

import com.hackathonteam1.refreshrator.annotation.AuthenticatedUser;
import com.hackathonteam1.refreshrator.authentication.AuthenticationExtractor;
import com.hackathonteam1.refreshrator.authentication.JwtEncoder;
import com.hackathonteam1.refreshrator.dto.ResponseDto;
import com.hackathonteam1.refreshrator.dto.request.auth.LoginDto;
import com.hackathonteam1.refreshrator.dto.request.auth.SigninDto;
import com.hackathonteam1.refreshrator.dto.response.auth.TokenResponseDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeListDto;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //회원 탈퇴
    @DeleteMapping("/leave")
    public ResponseEntity<ResponseDto<Void>> leave(@AuthenticatedUser User user,HttpServletResponse response) {
        authService.leave(user);

        ResponseCookie cookie = ResponseCookie.from("AccessToken", null)
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .sameSite("None").secure(true)
                .build();
        response.addHeader("set-cookie", cookie.toString());

        ResponseCookie cookie_refresh = ResponseCookie.from("RefreshToken",null)
                .maxAge(0)
                .path("/auth/refresh")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();
        response.addHeader("set-cookie", cookie_refresh.toString());

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
        ResponseCookie cookie = ResponseCookie.from("AccessToken", null)
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .sameSite("None").secure(true)
                .build();
        response.addHeader("set-cookie", cookie.toString());

        ResponseCookie cookie_refresh = ResponseCookie.from("RefreshToken",null)
                .maxAge(0)
                .path("/auth/refresh")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();
        response.addHeader("set-cookie", cookie_refresh.toString());

        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "로그아웃 완료"), HttpStatus.OK);
    }

    // 좋아요 누른 레시피 목록 조회
    @GetMapping("/likes")
    public ResponseEntity<ResponseDto<RecipeListDto>> showAllRecipeLikes(@AuthenticatedUser User user,
                                                                         @RequestParam(name = "page", defaultValue = "0")int page,
                                                                         @RequestParam(name = "size", defaultValue = "10")int size) {
        RecipeListDto recipeListDto = authService.showAllRecipeLikes(user, page, size);
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "좋아요 누른 레시피 목록 조회 성공", recipeListDto), HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<ResponseDto<Void>> refresh(HttpServletRequest request, HttpServletResponse response){
        TokenResponseDto tokenResponseDto = authService.refresh(request);
        String bearerToken = JwtEncoder.encodeJwtToken(tokenResponseDto.getAccessToken());

        ResponseCookie cookie_access = ResponseCookie.from(AuthenticationExtractor.TOKEN_COOKIE_NAME, bearerToken)
                .maxAge(Duration.ofMillis(1800000))
                .path("/")
                .httpOnly(true)
                .sameSite("None").secure(true)
                .build();

        ResponseCookie cookie_refresh = ResponseCookie.from("RefreshToken", tokenResponseDto.getRefreshToken().getTokenId().toString())
                .maxAge(Duration.ofDays(14))
                .path("/auth/refresh")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();

        response.addHeader("set-cookie", cookie_access.toString());
        response.addHeader("set-cookie", cookie_refresh.toString());
        return new ResponseEntity<>(ResponseDto.res(HttpStatus.OK, "토큰 재발급 성공"), HttpStatus.OK);
    }
}
