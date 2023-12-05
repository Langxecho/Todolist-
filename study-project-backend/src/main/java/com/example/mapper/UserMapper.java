package com.example.mapper;

import com.example.entity.auth.Account;
import com.example.entity.user.AccountUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author 19086
 * @version 1.0
 * Create by 2023/8/11 14:37
 */

@Mapper
public interface UserMapper {
    @Select("select * from db_account where username = #{text} or email = #{text}")
    Account findAccountByNameOrEmail(String text);

    @Insert("insert into db_account (email,username,password) values (#{email},#{username},#{password})")
    int createAccount(String username,String password,String email);

    @Select("select * from db_account where username = #{text} or email = #{text}")
    AccountUser findAccountUserByNameOrEmail(String text);
    @Update("update db_account set password = #{password} where email = #{email}")
    int resetPasswordByEmail(String password,String email);


}
