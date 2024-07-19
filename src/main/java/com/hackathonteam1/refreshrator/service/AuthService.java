package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.dto.request.SigninDto;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.exception.ConflictException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    //회원가입
    public void signin(SigninDto signinDto){

        //아이디(이메일) 중복 방지
        if(userRepository.findByEmail(signinDto.getEmail()).isPresent()){
            throw new ConflictException(ErrorCode.DUPLICATED_EMAIL);
        }

        //비밀번호 암호화

        //유저 생성과 등록
        User user=User.builder()
                .name(signinDto.getName())
                .email(signinDto.getEmail())
                .password(signinDto.getPassword())
                .build();
        userRepository.save(user);
    }
}
