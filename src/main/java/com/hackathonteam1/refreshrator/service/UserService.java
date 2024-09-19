package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.authentication.PasswordHashEncryption;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.exception.ConflictException;
import com.hackathonteam1.refreshrator.exception.NotFoundException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordHashEncryption passwordHashEncryption;

    //레시피 or 재료를 등록한 유저인지 확인
    public Boolean isAuthorized(User writer, User user){
        if(!writer.getId().equals(user.getId())){
            return false;
        }
        return true;
    }

    //회원가입시 아이디(이메일) 중복 검사
    public void checkEmailDuplicated(String email){
        if(userRepository.existsByEmail(email)){
            throw new ConflictException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    //로그인을 위한 아이디(이메일) 검사
    public User checkUserByEmail(String email,String password){

        String hashedPassword = passwordHashEncryption.encrypt(password);

        User user=userRepository.findByEmailAndPassword(email,hashedPassword);
        if(user==null){
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        return user;
    }
}

