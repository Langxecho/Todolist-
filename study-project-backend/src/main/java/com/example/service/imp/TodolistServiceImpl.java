package com.example.service.imp;

import com.example.entity.RestBean;
import com.example.entity.todolist.CreateMission;
import com.example.entity.todolist.Mission;
import com.example.mapper.TodolistMapper;
import com.example.service.TodolistService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author 19086
 * @version 1.0
 * Create by 2023/11/5 15:48
 */
@Service
@Slf4j
public class TodolistServiceImpl implements TodolistService {
    @Resource
    TodolistMapper todolistMapper;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    public List<Mission> getAllMissions(String username) {
        List<Mission> missions = todolistMapper.getUserMissionByUsername(username);
//        sortMissions(missions);
        return missions;
    }

    @Override
    public int updateMissionStatus(String headline, int num) {
        return todolistMapper.updateStatusByHeadline(headline, num);
    }

    @Override
    public int delMissionByHeandline(String headline) {
        return todolistMapper.deleteMissionByHeadline(headline);
    }

    @Override
    public void sortMissions(List<Mission> missions) {
        Collections.sort(missions, new Comparator<Mission>() {
            @Override
            public int compare(Mission o1, Mission o2) {
                if(System.currentTimeMillis() >= o1.getEndtime().getTime())
                    o1.setStatus(1);
                else
                    o1.setStatus(0);
                if(System.currentTimeMillis() >= o2.getEndtime().getTime())
                    o2.setStatus(1);
                else
                    o2.setStatus(0);
                long time1 = o1.getEndtime().getTime() - o1.getStarttime().getTime();
                long time2 = o2.getEndtime().getTime() - o2.getStarttime().getTime();
                return Long.compare(time1,time2);
            }
        });
    }

    @Override
    public int addNewMission(String username, String headline, String content, LocalDate starttime, LocalDate endtime, String tag, int status) {
        return todolistMapper.addNewMission(username, headline, content, starttime, endtime, tag, status);
    }

    @Override
    public int updateMission(String headline, String content, LocalDate starttime, LocalDate endtime, String tag, int status, String username) {
        return todolistMapper.updateMission(headline, content, starttime, endtime, tag, status, username);
    }

    @Override
    public List<Mission> searchMissions(String key,String username) {
        List<Mission> list = todolistMapper.getUserMissionByUsername(username);
        List<Mission> result = new ArrayList<>();
        if ('#' == key.charAt(0)){
            key = key.replace("#","");
            String pack[] = key.split(";");//tag分割后得到的数组
            log.info(pack[0]);
            boolean flag = false;
            for(int i = 0;i < list.size();i++){
                for(int j = 0;j < pack.length;j ++){
                    if(list.get(i).getTag().contains(pack[j])) flag = true;
                }
                if(flag) result.add(list.get(i));
                flag = false;
            }
        }else{
            String finalKey = key;
            list.forEach(mission -> {
                if(mission.getHeadline().contains(finalKey)) result.add(mission);
            });
        }
        return result;
        }
    @Override
    public int createNewMission(String username, LocalDate createtime, String headline) {
       return todolistMapper.createNewMission(username,createtime,headline);
    }

    @Override
    public int deleteNewMission(String headline) {
        return todolistMapper.deleteNewMission(headline);
    }

    @Override
    public List<CreateMission> findCreateMission(String username) {
        return todolistMapper.findCreateMission(username);
    }

    @Override
    public List<Mission> searchMissionsByDate(String dateKey, String username) {
        List<Mission> list = todolistMapper.getUserMissionByUsername(username);
        List<Mission> result = new ArrayList<>();
        list.forEach(mission -> {
            log.info(sdf.format(mission.getStarttime()));
            if(sdf.format(mission.getStarttime()).equals(dateKey))
                result.add(mission);
        });
        return result;
    }
}
