package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.todolist.CreateMission;
import com.example.entity.todolist.Mission;
import com.example.mapper.TodolistMapper;
import com.example.service.TodolistService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.In;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author 19086
 * @version 1.0
 * Create by 2023/11/5 10:47
 */
@RestController
@RequestMapping("/todolist")
@Slf4j
public class TodolistController {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Resource
    TodolistService todolistService;
    @PostMapping("/get-missions")
    //当前用户所有的接口
    public RestBean<List<Mission>> getMissions(@RequestParam("username") String username){
        List<Mission> missions = todolistService.getAllMissions(username);

            return RestBean.success(missions);
    }

    //删除指定的接口，>0成功，0<失败,这个数字其实代表行数
    @PostMapping("/del-mission")
    public RestBean<Integer> delMission(@RequestParam("headline") String headline){
        int temp = todolistService.delMissionByHeandline(headline);
        if (temp > 0){
            return RestBean.success(temp);
        }else{
            return RestBean.failure(temp);
        }
    }

    //更改任务完成的状态
    @PostMapping("/change-status")
    public RestBean<Integer> changeStatus(@RequestParam String headline,@RequestParam int num){
        int temp = todolistService.updateMissionStatus(headline, num);
        if (temp > 0){
            return RestBean.success(temp);
        }else{
            return RestBean.failure(403,temp);
        }
    }
    //增加一条新的任务
    @PostMapping("/new-mission")
    public RestBean<Integer> addNewMission(@RequestParam String username,
                                           @RequestParam String headline,
                                           @RequestParam String content,
                                           @RequestParam String starttime,
                                           @RequestParam String endtime,
                                           @RequestParam String tag,
                                           @RequestParam int status)
        {
            int temp = todolistService.addNewMission(username,
                                                    headline,
                                                    content,
                                                    LocalDate.parse(starttime,formatter),
                                                    LocalDate.parse(endtime,formatter),
                                                    tag,
                                                    status);
            if (temp > 0){
                return RestBean.success(temp);
            }else{
                return RestBean.failure(403,temp);
            }
    }
    @PostMapping("/create-new-mission")
    public RestBean<Integer> createNewMission(@RequestParam String username,
                                                    @RequestParam String createtime,
                                                    @RequestParam String headline
                                                    ){
        int temp = todolistService.createNewMission(username,LocalDate.parse(createtime,formatter),headline);
        if(temp > 0){return RestBean.success();}
        else  return RestBean.failure(500,temp);
    }
    @PostMapping("/get-create-mission")
    public RestBean<List<CreateMission>> findNewMission(@RequestParam String username){
        List<CreateMission> data = todolistService.findCreateMission(username);
        if (!data.isEmpty()) {
            return RestBean.success(data);
        }else {return RestBean.failure(500,null);}
    }
    @PostMapping("/delete-create-mission")
    public RestBean<Integer> deleteCreateMission(@RequestParam String headline){
        int temp = todolistService.deleteNewMission(headline);
        if (temp > 0) return RestBean.success();
        else return RestBean.failure(500,temp);
    }
    @PostMapping("/search-by-key")
    public RestBean<List<Mission>> searchByKey(@RequestParam String key,
                                               @RequestParam String user){

        return RestBean.success(todolistService.searchMissions(key, user));
    }
    @PostMapping("/search-by-time")
    public RestBean<List<Mission>> searchByTime(@RequestParam String datekey,
                                               @RequestParam String user){

        return RestBean.success(todolistService.searchMissionsByDate(datekey, user));
    }
    @PostMapping("/update-mission")
    public RestBean<Integer> updateMission(@RequestParam String username,
                                           @RequestParam String headline,
                                           @RequestParam String content,
                                           @RequestParam String starttime,
                                           @RequestParam String endtime,
                                           @RequestParam String tag,
                                           @RequestParam int status){
        return RestBean.success(todolistService.updateMission(headline,
                content,
                LocalDate.parse(starttime,formatter),
                LocalDate.parse(endtime,formatter),
                tag,status,username));
    }
}
