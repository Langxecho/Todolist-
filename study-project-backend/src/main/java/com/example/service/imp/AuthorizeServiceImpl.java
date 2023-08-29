package com.example.service.imp;

import com.example.entity.Account;
import com.example.mapper.UserMapper;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.String.valueOf;

/**
 * @author 19086
 * @version 1.0
 * Create by 2023/8/11 14:35
 */
@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Resource
    UserMapper mapper;//创建usermapper对象并自动注入bean
    @Resource
    MailSender mailSender;
    @Value("${spring.mail.username}")
    String from;
    @Resource
    StringRedisTemplate template;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Override
    //这个方法是通过数据库来查询用户名密码来进行登录
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username == null) throw new UsernameNotFoundException("用户名不能为空");
        Account account = mapper.findAccountByNameOrEmail(username);
        if (account == null) throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();
    }
    /**
     1.先生成对应的验证码
     2.把邮箱和对应的验证码放到redis里面，过期时间3min（如果重新要求发右键，只要剩余时间低于2min就可以重新
     发送一次，重复此流程）
     3.发送验证码到指定邮箱
     4.如果发送失败，把redis里面刚刚插入的给删除
     5.用户在注册时再从redistribution里面取出对应键值对，然后看验证码是否一致
     **/
    @Override
    public String sendValidateEmail(String email,String sessionId) {
        String key = "email:" + sessionId + ":" + email;
        if(Boolean.TRUE.equals(template.hasKey(key))){
            Long expire = Optional.ofNullable(template.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if (expire > 120){return "请求频繁，请1分钟后尝试";}
        }
        if(mapper.findAccountByNameOrEmail(email) != null){
            return "此邮箱已被注册";
        }
        Random random = new Random();
        int code = random.nextInt(899999) + 100000;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("您的验证邮件");
        message.setText("验证码是：" + code);
        try{
            mailSender.send(message);

            template.opsForValue().set(key,String.valueOf(code),3, TimeUnit.MINUTES);
            return null;
        }catch (MailException e){
            e.printStackTrace();
            return "邮件发送失败，请联系管理员";
        }

    }

    @Override
    public String validateAndRegister(String username, String password, String email, String code,String sessionId) {
        String key = "email:" + sessionId + ":" + email;
        if (Boolean.TRUE.equals(template.hasKey(key))){
            String s = template.opsForValue().get(key);
            if (s == null) return "邮件验证码失效，请重新请求";
            if (s.equals(code)){
                password = bCryptPasswordEncoder.encode(password);
                if (mapper.createAccount(username,password,email) > 0) {
                    return null;
                }
                else {
                    return "内部错误，请联系管理员";
                }
            }else {
                return "验证码错误，请先检查后再提交";
            }
        }else{
            return "请先请求一封验证码邮件";
        }

    }


}
