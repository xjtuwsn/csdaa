package com.csdaa.wsn.server.controller;

import com.csdaa.wsn.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @project:csdaa-server
 * @file:UserController
 * @author:wsn
 * @create:2022/5/31-21:26
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @RequestMapping("/login")
    public byte[] login(HttpServletRequest request){
        return userService.login(request);
    }
    @RequestMapping("/islogin")
    public byte[] islogin(HttpServletRequest request){
        return userService.islogin(request);
    }
    @RequestMapping("/register")
    public byte[] register(HttpServletRequest request){
        return userService.register(request);
    }
}
