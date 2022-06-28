package com.csdaa.wsn.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @project:csdaa-client
 * @file:LoginMessage
 * @author:wsn
 * @create:2022/5/31-19:09
 */
@Data
@Getter
@Setter
@AllArgsConstructor
public class LoginMessage implements Serializable {
    public String dataID;
    public String username;
    public String password;
    public String token;
    public String flag;
}
