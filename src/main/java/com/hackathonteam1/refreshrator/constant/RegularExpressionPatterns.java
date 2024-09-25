package com.hackathonteam1.refreshrator.constant;

public class RegularExpressionPatterns {
    //아이디(이메일)
    public static final String EMAIL_PATTERN= "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{1,100}$";
    //비밀번호
    public static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,14}$";
}
