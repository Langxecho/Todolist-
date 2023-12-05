package com.example.entity.auth;

import lombok.Data;

/**
 * @author 19086
 * @version 1.0
 * Create by 2023/8/11 14:38
 */
@Data
public class Account {
    int id;
    String email;
    String username;
    String password;
}
