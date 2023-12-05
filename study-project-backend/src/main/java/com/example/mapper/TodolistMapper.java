package com.example.mapper;

import com.example.entity.todolist.CreateMission;
import com.example.entity.todolist.Mission;
import org.apache.ibatis.annotations.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.List;

/**
 * @author 19086
 * @version 1.0
 * Create by 2023/11/4 16:12
 */

@Mapper
public interface TodolistMapper {
    @Insert("insert into todolist value (#{username},#{headline},#{content},#{starttime},#{endtime},#{tag},#{status})")
    int addNewMission(String username, String headline, String content, LocalDate starttime, LocalDate endtime, String tag, int status);

    @Update("UPDATE todolist SET content = #{content}, starttime = #{starttime}, endtime = #{endtime}, tag = #{tag}, status = #{status} WHERE username = #{username} and headline = #{headline}")
    int updateMission(String headline, String content, LocalDate starttime, LocalDate endtime, String tag, int status,String username);
    @Select("select * from todolist where username = #{username}")
    List<Mission> getUserMissionByUsername(String username);

    @Delete("delete from todolist where headline = #{headline}")
    int deleteMissionByHeadline(String headline);

    @Update("update todolist set status = #{status} where headline = #{headline}")
    int updateStatusByHeadline(String headline,int status);

    @Insert("insert into mission_create value (#{username},#{createtime},#{headline})")
    int createNewMission(String username,LocalDate createtime,String headline);

    @Delete("delete from mission_create where headline = #{headline}")
    int deleteNewMission(String headline);

    @Select("select * from mission_create where username = #{username}")
    List<CreateMission> findCreateMission(String username);
}
