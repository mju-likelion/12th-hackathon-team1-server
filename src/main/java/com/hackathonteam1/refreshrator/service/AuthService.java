package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.authentication.PasswordHashEncryption;
import com.hackathonteam1.refreshrator.dto.request.auth.LoginDto;
import com.hackathonteam1.refreshrator.dto.request.auth.SigninDto;
import com.hackathonteam1.refreshrator.entity.*;
import com.hackathonteam1.refreshrator.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordHashEncryption passwordHashEncryption;
    private final ImageService imageService;
    private final UserService userService;

    //회원가입
    public void signin(SigninDto signinDto){

        //아이디(이메일) 중복 방지
        userService.checkEmailDuplicated(signinDto.getEmail());

        //비밀번호 암호화
        String plainPassword = signinDto.getPassword();
        String hashedPassword = passwordHashEncryption.encrypt(plainPassword);

        //유저 생성과 냉장고 등록
        User signinUser= new User(signinDto.getEmail(),hashedPassword,signinDto.getName());
        userRepository.save(signinUser);
    }

    //회원탈퇴
    public void leave(User user){
        //레시피를 삭제하기 전, 유저의 레시피 내 이미지를 S3에서 모두 삭제
        if(user.getRecipes()!=null){
            imageService.deleteAllImagesOfUser(user);
        }
        //탈퇴
        userRepository.delete(user);
    }

    //로그인
    public UUID login(LoginDto loginDto){

        //아이디(이메일)검사
        User user=userService.checkUserByEmail(loginDto.getEmail());

        ////비밀번호가 입력한 아이디(이메일)에 일치하는지 검사
        userService.checkPassword(loginDto.getPassword(), user.getPassword());

        return user.getId();
    }
}
