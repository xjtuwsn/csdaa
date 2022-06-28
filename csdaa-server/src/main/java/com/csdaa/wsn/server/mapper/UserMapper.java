package com.csdaa.wsn.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {
    Integer getUserID(String username,String password);
    String getUserFlag(Integer id);
    Integer getIdByUsername(String username);
    Integer addUser(String username,String password,String flag);
}
