package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
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
    @Resource
    AuthorizeService service;

    @PostMapping("/valid-email")
    public RestBean<String> ValidateEmail(@Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                          HttpSession session){
        if (service.sendValidateEmail(email,session.getId())) return RestBean.success("邮件已发送，请注意查收");
        else return RestBean.failure(400,"邮件发送失败，请联系管理员");
    }
}
