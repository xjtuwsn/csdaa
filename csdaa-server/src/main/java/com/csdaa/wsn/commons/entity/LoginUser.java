package com.csdaa.wsn.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @project:csdaa-server
 * @file:LoginUser
 * @author:wsn
 * @create:2022/5/31-20:00
 */
@Data
@Getter
@Setter
@AllArgsConstructor
public class LoginUser {
    public String userId;
    public String tag;
}
