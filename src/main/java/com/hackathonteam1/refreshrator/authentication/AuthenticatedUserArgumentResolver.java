package com.hackathonteam1.refreshrator.authentication;

import com.hackathonteam1.refreshrator.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthenticationContext authenticationContext;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedUser.class);
    }

    //supportsParameter가 true를 반환할 때 호출
    @Override
    public User resolveArgument(final MethodParameter parameter,    //메서드의 파라미터를 나타내는 객체
                                final ModelAndViewContainer mavContainer,   //현재 요청 모델의 뷰 정보를 담고 있음
                                final NativeWebRequest webRequest,  //HTTP요청을 추상화한 객체
                                final WebDataBinderFactory binderFactory) { //데이터바인더를 반환하는 팩토리
        return authenticationContext.getPrincipal();    //이제 인증된 User를 반환
    }
}

