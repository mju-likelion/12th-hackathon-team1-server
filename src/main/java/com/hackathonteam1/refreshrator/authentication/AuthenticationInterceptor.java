package com.hackathonteam1.refreshrator.authentication;


import com.hackathonteam1.refreshrator.entity.User;
import com.hackathonteam1.refreshrator.exception.UnauthorizedException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import com.hackathonteam1.refreshrator.repository.UserRepository;
import com.hackathonteam1.refreshrator.util.AccessTokenUtil;
import com.hackathonteam1.refreshrator.util.UriMatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final AuthenticationContext authenticationContext;
    private final UserRepository userRepository;
    private final AccessTokenUtil accessTokenUtil;

    public static final String TOKEN_COOKIE_NAME = "AccessToken";
    private final static Set<String> EXCLUDE_RECIPES_PATTENS = new HashSet<>(Arrays.asList("/recipes/recommendations"));

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) {

        if(CorsUtils.isPreFlightRequest(request)){ //preflight OPTIONS 요청을 위함.
            return true;
        }

        UriMatcher recipeListUriMatcher = new UriMatcher(HttpMethod.GET, "/recipes");
        UriMatcher recipeDetailUriMatcher = new UriMatcher(HttpMethod.GET, "/recipes/{id}");

        if(recipeListUriMatcher.match(request)) return true; //Request 경로가 UriMatcher와 일치한다면 통과.
        if(recipeDetailUriMatcher.matchWithExclusion(request, EXCLUDE_RECIPES_PATTENS)) return true; //Request 경로가 제외 패턴에 해당하지않고 UriMatcher와 일치한다면 통과.

        String accessToken = AuthenticationExtractor.extract(request, TOKEN_COOKIE_NAME);
        UUID userId = UUID.fromString(accessTokenUtil.getPayload(accessToken));
        User user = findExistingUser(userId);
        authenticationContext.setPrincipal(user);
        return true;
    }

    private User findExistingUser(final UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_TOKEN));
    }
}