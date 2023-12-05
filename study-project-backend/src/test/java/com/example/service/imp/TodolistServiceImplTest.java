package com.example.service.imp;

import com.example.entity.todolist.Mission;
import com.example.mapper.TodolistMapper;
import com.example.service.TodolistService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
class TodolistServiceImplTest {
    @Resource
    TodolistService todolistService;
    @Resource
    TodolistMapper todolistMapper;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    void searchMissions() {

        log.info(todolistService.searchMissions("123","user").toString());
    }

    @Test
    void searchMissionsByDate() {
        List<Mission> list = todolistMapper.getUserMissionByUsername("user");
        list.forEach(mission -> {
        });
        log.info(todolistService.searchMissions("2023-11-10","user").toString());
    }

}