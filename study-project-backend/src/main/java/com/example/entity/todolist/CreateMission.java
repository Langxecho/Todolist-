package com.example.entity.todolist;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author 19086
 * @version 1.0
 * Create by 2023/11/17 8:17
 */
@Data
public class CreateMission {
    String username;
    LocalDate createTime;
    String headline;

}
