package com.example.mapper;

import com.example.entity.todolist.Mission;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
class TodolistMapperTest {
    @Resource
    TodolistMapper todolistMapper;
    @Test
    void addNewMission() {
        System.out.println(todolistMapper.addNewMission("z", "z", "z", LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), "z", 0));
    }

    @Test
    void getUserMissionByUsername() {
        log.info(todolistMapper.getUserMissionByUsername("user").toString());

    }

    @Test
    void deleteMissionByHeadline() {
        todolistMapper.deleteMissionByHeadline("z");
    }

    @Test
    void updateStatusByHeadline() {
        todolistMapper.updateStatusByHeadline("这是一条任务",1);
        Mission mission = new Mission();
        mission.getHeadline();
        List<Mission> list = new ArrayList<>();

    }

    @Test
    void createNewMission() {
        todolistMapper.createNewMission("user",LocalDate.of(2221,1,1),"任务测试");
    }

    @Test
    void deleteNewMission() {
        System.out.println(todolistMapper.deleteNewMission("任务测试"));
    }

    @Test
    void findCreateMission() {
        System.out.println(todolistMapper.findCreateMission("user"));
    }
}