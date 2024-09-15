package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.authentication.PasswordHashEncryption;
import com.hackathonteam1.refreshrator.dto.request.auth.LoginDto;
import com.hackathonteam1.refreshrator.dto.request.auth.SigninDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeListDto;
import com.hackathonteam1.refreshrator.entity.*;
import com.hackathonteam1.refreshrator.exception.ConflictException;
import com.hackathonteam1.refreshrator.exception.ForbiddenException;
import com.hackathonteam1.refreshrator.exception.NotFoundException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final FridgeRepository fridgeRepository;
    private final PasswordHashEncryption passwordHashEncryption;
    private final RecipeLikeRepository recipeLikeRepository;
    private final ImageService imageService;


    //회원가입
    public void signin(SigninDto signinDto){

        //아이디(이메일) 중복 방지
        checkEmailDuplicated(signinDto.getEmail());

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
        User user=userRepository.findByEmail(loginDto.getEmail());

        if(user==null){
            throw new NotFoundException(ErrorCode.USERID_NOT_FOUND);
        }

        //비밀번호가 입력한 아이디에 일치하는지 검사
        if(!passwordHashEncryption.matches(loginDto.getPassword(), user.getPassword())){
            throw new ForbiddenException(ErrorCode.INVALID_PASSWORD);
        }

        return user.getId();
    }

    // 좋아요 누른 레시피 목록 조회
    public RecipeListDto showAllRecipeLikes(User user, int page, int size) {
        List<RecipeDto> recipeLists = new ArrayList<>();

        Sort sort = Sort.by(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page, size);

        Page<RecipeLike> recipeLikes = this.recipeLikeRepository.findAllByUser(user, pageable);
        List<Recipe> recipes = recipeLikes.stream().map(like -> like.getRecipe()).collect(Collectors.toList());
        Page<Recipe> recipePage = new PageImpl<>(recipes);

        checkValidPage(recipePage, page);

        RecipeListDto recipeListDto = RecipeListDto.mapping(recipePage);
        return recipeListDto;
    }

    private <T> void checkValidPage(Page<T> pages, int page){
        if(pages.getTotalPages() <= page && page != 0){
            throw new NotFoundException(ErrorCode.PAGE_NOT_FOUND);
        }
    }

    private void checkEmailDuplicated(String email){
        if(userRepository.existsByEmail(email)){
            throw new ConflictException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

}
