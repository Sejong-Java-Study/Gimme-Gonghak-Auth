package com.example.gimmegonghakauth.controller;

import com.example.gimmegonghakauth.dao.MajorsDao;
import com.example.gimmegonghakauth.domain.UserCreateForm;
import com.example.gimmegonghakauth.domain.UserDomain;
import com.example.gimmegonghakauth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final MajorsDao majorsDao;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form"; //회원가입시 비밀번호 확인
        }
        //회원 정보 저장
        userService.create(userCreateForm.getStudentId(), userCreateForm.getPassword1(),
            userCreateForm.getEmail(),
            majorsDao.findByMajor(userCreateForm.getMajor()), userCreateForm.getName());
        return "redirect:/user/signup"; //현재는 일시적으로 회원가입 페이지로 이동하도록 설정해놓음
    }
}