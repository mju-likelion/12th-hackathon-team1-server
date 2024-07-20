package com.hackathonteam1.refreshrator.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SigninDto {
    //이름
    @NotBlank(message = "본인의 이름을 입력해주세요.")
    @Size(min = 1, max = 20, message = "이름은 최소 한글자 최대 20글자입니다.")
    private String name;

    //아이디(이메일)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{1,100}$", message = "이메일이 형식에 맞지 않습니다.")
    @NotBlank(message = "사용하실 아이디(이메일)을 입력해주세요.")
    @Size(min = 1,max=100,message = "아이디는 최소 한글자 이상 최대 100글자입니다.")
    private String email;

    //비밀번호
    @NotBlank(message = "영문과 숫자,특수기호를 조합하여 8~14글자 미만으로 입력하여 주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,14}$", message = "영문,숫자,특수기호를 조합하여 8~14글자 미만으로 입력하여 주세요.")
    @Size(min = 8, max = 14, message = " 비밀번로는 최소8글자 최대 14글자 입니다.")
    private String password;

}
