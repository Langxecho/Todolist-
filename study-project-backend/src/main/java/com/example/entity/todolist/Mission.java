package com.example.entity.todolist;

import lombok.Data;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
@Data
public class Mission{
    String username;
    String headline;
    String content;
    Date starttime;
    Date endtime;
    String tag;
    int status;
}
