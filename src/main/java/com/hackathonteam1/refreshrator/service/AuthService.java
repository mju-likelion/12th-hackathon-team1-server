package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.authentication.JwtTokenProvider;
import com.hackathonteam1.refreshrator.authentication.PasswordHashEncryption;
import com.hackathonteam1.refreshrator.dto.request.auth.LoginDto;
import com.hackathonteam1.refreshrator.dto.request.auth.SigninDto;
import com.hackathonteam1.refreshrator.dto.response.auth.TokenResponseDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeListReponseDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeResponseDto;
import com.hackathonteam1.refreshrator.entity.Fridge;
import com.hackathonteam1.refreshrator.entity.RecipeLike;
import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.exception.ConflictException;
import com.hackathonteam1.refreshrator.exception.ForbiddenException;
import com.hackathonteam1.refreshrator.exception.NotFoundException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.FridgeRepository;
import com.hackathonteam1.refreshrator.repository.RecipeLikeRepository;
import com.hackathonteam1.refreshrator.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final FridgeRepository fridgeRepository;
    private final PasswordHashEncryption passwordHashEncryption;
    private final JwtTokenProvider jwtTokenProvider;
    private final RecipeLikeRepository recipeLikeRepository;

    //회원가입
    public void signin(SigninDto signinDto){

        //아이디(이메일) 중복 방지
        User user=userRepository.findByEmail(signinDto.getEmail());
        if(user!=null){
            throw new ConflictException(ErrorCode.DUPLICATED_EMAIL);
        }

        //비밀번호 암호화
        String plainPassword = signinDto.getPassword();
        String hashedPassword = passwordHashEncryption.encrypt(plainPassword);

        //유저 생성과 등록
        User signinUser=User.builder()
                .name(signinDto.getName())
                .email(signinDto.getEmail())
                .password(hashedPassword)
                .build();
        userRepository.save(signinUser);

        //냉장고 생성
        Fridge fridge= Fridge.builder()
                .user(signinUser)
                .build();
        fridgeRepository.save(fridge);
    }

    //회원탈퇴
    public void leave(User user){
        //탈퇴
        userRepository.delete(user);
    }

    //로그인
    public TokenResponseDto login(LoginDto loginDto){

        //아이디(이메일)검사
        User user=userRepository.findByEmail(loginDto.getEmail());

        if(user==null){
            throw new NotFoundException(ErrorCode.USERID_NOT_FOUND);
        }

        //비밀번호가 입력한 아이디에 일치하는지 검사
        if(!passwordHashEncryption.matches(loginDto.getPassword(), user.getPassword())){
            throw new ForbiddenException(ErrorCode.INVALID_PASSWORD);
        }

        //토큰 생성
        String payload = String.valueOf(user.getId());
        String accessToken = jwtTokenProvider.createToken(payload);

        return new TokenResponseDto(accessToken);
    }

    // 좋아요 누른 레시피 목록 조회
    public Page<RecipeResponseDto> showAllRecipeLikes(User user, Pageable pageable) {

        //정렬
//        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());

        List<RecipeResponseDto> recipeListReponseDtos = new ArrayList<>();
        Page<RecipeLike> recipeLikes = this.recipeLikeRepository.findAllByUser(user, pageable);

        for(RecipeLike recipeLike : recipeLikes){
            RecipeResponseDto recipeResponseDto = RecipeResponseDto.builder()
                    .recipeId(recipeLike.getRecipe().getId())
                    .name(recipeLike.getRecipe().getName())
                    .likeCount(recipeLike.getRecipe().getLikeCount())
                    .build();
            recipeListReponseDtos.add(recipeResponseDto);
        }

        return new PageImpl<>(recipeListReponseDtos, pageable, recipeLikes.getTotalElements());
    }
}
