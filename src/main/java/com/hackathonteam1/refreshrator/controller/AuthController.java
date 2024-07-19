package com.hackathonteam1.refreshrator.controller;

import com.hackathonteam1.refreshrator.dto.ResponseDto;
import com.hackathonteam1.refreshrator.dto.request.SigninDto;
import com.hackathonteam1.refreshrator.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<ResponseDto<Void>> signup(@RequestBody @Valid SigninDto signinDto) {

        authService.signin(signinDto);

        return new ResponseEntity<>(ResponseDto.res(HttpStatus.CREATED, "회원 가입 완료"), HttpStatus.CREATED);
    }
}
