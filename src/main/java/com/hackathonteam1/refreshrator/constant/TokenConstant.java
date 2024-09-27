package com.hackathonteam1.refreshrator.constant;

import java.time.Duration;

public class TokenConstant {

    //AccessToken
    public static final String ACCESS_TOKEN_NAME = "AccessToken";
    public static final Duration ACCESS_TOKEN_MAX_AGE = Duration.ofMinutes(30);
    public static final String ACCESS_TOKEN_PATH = "/";

    //RefreshToken
    public static final String REFRESH_TOKEN_NAME = "RefreshToken";
    public static final Duration REFRESH_TOKEN_MAX_AGE = Duration.ofDays(14);
    public static final String REFRESH_TOKEN_PATH = "/auth/refresh";

}
