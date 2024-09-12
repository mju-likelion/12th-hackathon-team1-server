package com.hackathonteam1.refreshrator.service;

import com.hackathonteam1.refreshrator.authentication.JwtTokenProvider;
import com.hackathonteam1.refreshrator.authentication.PasswordHashEncryption;
import com.hackathonteam1.refreshrator.dto.request.auth.LoginDto;
import com.hackathonteam1.refreshrator.dto.request.auth.SigninDto;
import com.hackathonteam1.refreshrator.dto.response.auth.TokenResponseDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeDto;
import com.hackathonteam1.refreshrator.dto.response.recipe.RecipeListDto;
import com.hackathonteam1.refreshrator.entity.*;
import com.hackathonteam1.refreshrator.exception.ConflictException;
import com.hackathonteam1.refreshrator.exception.ForbiddenException;
import com.hackathonteam1.refreshrator.exception.NotFoundException;
import com.hackathonteam1.refreshrator.exception.UnauthorizedException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.*;
import com.hackathonteam1.refreshrator.util.RedisUtil;
import com.hackathonteam1.refreshrator.util.S3Uploader;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final FridgeRepository fridgeRepository;
    private final PasswordHashEncryption passwordHashEncryption;
    private final JwtTokenProvider jwtTokenProvider;
    private final RecipeLikeRepository recipeLikeRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;

    private final RedisUtil<String, RefreshToken> redisUtilForRefreshToken;
    private final RedisUtil<String, String> redisUtilForUserId;

    private final static int TIMEOUT = 14;
    private final static TimeUnit TIME_UNIT = TimeUnit.DAYS;


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

        //기존에 refreshToken이 있었는지 확인 후 삭제
        Optional<String> refreshTokenId = redisUtilForUserId.findById(user.getId().toString());
        if(refreshTokenId.isPresent()){
            redisUtilForRefreshToken.delete(refreshTokenId.get());
            redisUtilForUserId.delete(user.getId().toString());
        }

        UUID newRefreshTokenId = UUID.randomUUID();
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenId(newRefreshTokenId)
                .userId(user.getId())
                .build();

        redisUtilForRefreshToken.save(newRefreshTokenId.toString(), refreshToken, TIMEOUT, TIME_UNIT);
        redisUtilForUserId.save(user.getId().toString(), newRefreshTokenId.toString(),TIMEOUT,TIME_UNIT);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponseDto refresh(HttpServletRequest request){

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("RefreshToken")) {
                    RefreshToken refreshToken = findRefreshTokenByRefreshTokenId(UUID.fromString(cookie.getValue()));
                    UUID userId = refreshToken.getUserId();
                    String accessToken = jwtTokenProvider.createToken(userId.toString());

                    //refreshToken Rotation을 위해 매번 재발급.
                    //refreshToken을 위해 redis에는 <key:userId, value:refreshTokenId>와
                    //<key:refreshTokenId, value:refreshToken>의 형태로 2개를 저장함
                    redisUtilForRefreshToken.delete(refreshToken.getTokenId().toString());
                    redisUtilForUserId.delete(userId.toString());

                    UUID newRefreshTokenId = UUID.randomUUID();

                    RefreshToken newRefreshToken = RefreshToken.builder()
                            .tokenId(newRefreshTokenId)
                            .userId(userId)
                            .build();

                    redisUtilForRefreshToken.save(newRefreshTokenId.toString(), newRefreshToken, TIMEOUT, TIME_UNIT);
                    redisUtilForUserId.save(userId.toString(), newRefreshTokenId.toString(),TIMEOUT,TIME_UNIT);

                    return new TokenResponseDto(accessToken, newRefreshToken);
                }
            }
        }
        throw new UnauthorizedException(ErrorCode.COOKIE_NOT_FOUND, "RefreshToken이 존재하지 않습니다.");
    }

    private RefreshToken findRefreshTokenByRefreshTokenId(UUID tokenId){
        return redisUtilForRefreshToken.findById(tokenId.toString()).orElseThrow( () ->
                new UnauthorizedException(ErrorCode.INVALID_TOKEN, "유효하지 않은 RefreshToken입니다."));
    }

    private Image findImageByRecipe(Recipe recipe){
        return imageRepository.findByRecipe(recipe).orElseThrow(()->new NotFoundException(ErrorCode.IMAGE_NOT_FOUND));
    }

    private void checkEmailDuplicated(String email){
        if(userRepository.existsByEmail(email)){
            throw new ConflictException(ErrorCode.DUPLICATED_EMAIL);
        }
    }
}
