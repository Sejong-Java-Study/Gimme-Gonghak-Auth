package com.example.gimmegonghakauth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {
    @NotBlank(message = "기존 비밀번호를 입력해주세요.")
    private String currentPassword;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    private String newPassword1;

    @NotBlank(message = "새로운 비밀번호를 다시 한번 입력해주세요.")
    private String newPassword2;

}
