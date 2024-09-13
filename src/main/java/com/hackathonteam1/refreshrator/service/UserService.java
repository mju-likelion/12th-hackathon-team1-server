package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.authentication.PasswordHashEncryption;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.exception.ForbiddenException;
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

    public User checkUserByEmail(String email){
        User user=userRepository.findByEmail(email);

        if(user==null){
            throw new NotFoundException(ErrorCode.USERID_NOT_FOUND);
        }

        return user;
    }

    //비밀번호가 입력한 아이디(이메일)에 일치하는지 검사
    public void checkPassword(String inputPassword,String UserPassword){

        if(!passwordHashEncryption.matches(inputPassword, UserPassword)){
            throw new ForbiddenException(ErrorCode.INVALID_PASSWORD);
        }
    }
}