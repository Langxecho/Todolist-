package com.example.interceptor;

import com.example.entity.user.AccountUser;
import com.example.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author 19086
 * @version 1.0
 * Create by 2023/8/31 9:29
 */
@Component
public class AuthorizeInterceptor implements HandlerInterceptor {
    //用户每次访问index以及其他页面时都需要向服务器发起请求查询当前是否为登录状态，controller当中的/me接口就是做这个的
    //现在的这个拦截器用于在请求之前检查用户是否处于登录状态，通过security框架检查上下文等操作来实现
    //如果用户没登陆会被security拦截下来，根本到不了prehandler这里
    @Resource
    UserMapper mapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        User user = (User)authentication.getPrincipal();
        String username = user.getUsername();
        AccountUser account = mapper.findAccountUserByNameOrEmail(username);
        request.getSession().setAttribute("account",account);
        return true;
    }
}
