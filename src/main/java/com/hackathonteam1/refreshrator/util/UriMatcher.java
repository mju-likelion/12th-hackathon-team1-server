package com.hackathonteam1.refreshrator.util;


import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriTemplate;

import java.util.Collection;

@AllArgsConstructor
public class UriMatcher {
    private final HttpMethod method;
    private final String path;

    public boolean matchWithExclusion(HttpServletRequest request, Collection<String> excludePatterns){
        if(excludePatterns.stream().anyMatch(i->i.equals(request.getRequestURI()))) return false;
        UriTemplate uriTemplate = new UriTemplate(path);
        return method.matches(request.getMethod()) && uriTemplate.matches(request.getRequestURI());
    }

    public boolean match(HttpServletRequest request){
        UriTemplate uriTemplate = new UriTemplate(path);
        return method.matches(request.getMethod()) && uriTemplate.matches(request.getRequestURI());
    }
}