package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 19086
 * @version 1.0
 * Create by 2023/8/24 9:16
 */
@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthorizeController {
    private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final String USERNAME_REGEX= "^[A-Za-z\\u4E00-\\u9FA5]+$";
    @Resource
    AuthorizeService service;

    @PostMapping("/valid-email")
    public RestBean<String> ValidateEmail(@Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                          HttpSession session){
        String s = service.sendValidateEmail(email, session.getId());
        if (s == null) return RestBean.success("邮件已发送，请注意查收");
        else return RestBean.failure(400,s);
    }
    @PostMapping("/register")
    public RestBean<String> registerUser(@Pattern(regexp = USERNAME_REGEX) @Length(min = 2,max = 8)@RequestParam("username") String username,
                                         @Length(min = 6,max = 16)@RequestParam("password") String password,
                                         @Pattern(regexp = EMAIL_REGEX)@RequestParam("email") String email,
                                         @Length(min = 6,max = 6)@RequestParam("code") String code,
                                         HttpSession session){
        String s = service.validateAndRegister(username, password, email, code, session.getId());
        if(s == null)
            return  RestBean.success("注册成功");
        else
            return RestBean.failure(400,s);

    }
}
