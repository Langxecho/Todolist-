package com.example.service;

import com.example.entity.todolist.CreateMission;
import com.example.entity.todolist.Mission;

import java.time.LocalDate;
import java.util.List;

public interface TodolistService {
    //获取当前用户的所有任务
    public List<Mission> getAllMissions(String username);
    //删除指定行
    public int delMissionByHeandline(String headline);
    //修改任务完成状态
    public int updateMissionStatus(String headline,int num);
    public int addNewMission(String username, String headline, String content, LocalDate starttime, LocalDate endtime, String tag, int status);
    public List<Mission> searchMissions(String key,String username);
    public List<Mission> searchMissionsByDate(String dateKey,String username);
    public int createNewMission(String username,LocalDate createtime,String headline);
    public int deleteNewMission(String headline);
    public List<CreateMission> findCreateMission(String username);
    public int updateMission(String headline, String content, LocalDate starttime, LocalDate endtime, String tag, int status,String username);
    //任务排序
    public void sortMissions(List<Mission> missions);

}
