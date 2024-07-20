package com.hackathonteam1.refreshrator.config;

import com.hackathonteam1.refreshrator.annotation.resolver.AuthenticatedUserArgumentResolver;
import com.hackathonteam1.refreshrator.authentication.AuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final AuthenticatedUserArgumentResolver authenticatedUserArgumentResolver;

    //인터셉터 등록 + 경로 설정
    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/fridge/**","/recipes/**")
                .excludePathPatterns("/auth/signin", "/auth/login","/auth/logout");
    }

    //컨트롤러 메서드 파라미터에 인증된 유저가 들어가도록 함
    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedUserArgumentResolver);
    }
}
