package com.csdaa.wsn.server.service;

import com.csdaa.wsn.commons.entity.LoginMessage;
import com.csdaa.wsn.commons.entity.LoginUser;
import com.csdaa.wsn.commons.entity.ResponseMsg;
import com.csdaa.wsn.server.config.TempSys;
import com.csdaa.wsn.server.mapper.UserMapper;
import com.csdaa.wsn.server.utils.HttpUtil;
import com.csdaa.wsn.server.utils.RSAUtils;
import com.csdaa.wsn.server.utils.SerializeUtil;
import com.csdaa.wsn.server.utils.TokenUtil;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;

/**
 * @project:csdaa-server
 * @file:UserService
 * @author:wsn
 * @create:2022/5/31-21:26
 */
@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    public byte[] login(HttpServletRequest request){
        LoginMessage message=null;
        try {
            message=(LoginMessage)SerializeUtil.doDeserialize(HttpUtil.getData(request));
            RSAPrivateKey privateKey = RSAUtils.getPrivateKey(TempSys.privateKey);
            String username = RSAUtils.decryptByPrivateKey(message.username, privateKey);
            String password = RSAUtils.decryptByPrivateKey(message.password, privateKey);
            Integer userID = userMapper.getUserID(username, password);
            if(userID!=null){
                String userFlag = userMapper.getUserFlag(userID);
                LoginUser loginUser = new LoginUser(userID + "", userFlag);
                String sign = TokenUtil.sign(loginUser);
                System.out.println("得到token，值为："+sign);
                ResponseMsg responseMsg = new ResponseMsg(987, message.dataID, sign);
                return SerializeUtil.doSerialize(responseMsg);
            }
            return SerializeUtil.doSerialize(new ResponseMsg(985,message.dataID));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public byte[] islogin(HttpServletRequest request){
        LoginMessage message=null;
        try {
            message= (LoginMessage) SerializeUtil.doDeserialize(HttpUtil.getData(request));

            Boolean verify = TokenUtil.verify(message.token);
            if(verify){
                return SerializeUtil.doSerialize(new ResponseMsg(976,message.dataID));
            }
            return SerializeUtil.doSerialize(new ResponseMsg(975, message.dataID));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public byte[] register(HttpServletRequest request){
        LoginMessage message=null;
        try {
            message=(LoginMessage)SerializeUtil.doDeserialize(HttpUtil.getData(request));
            RSAPrivateKey privateKey = RSAUtils.getPrivateKey(TempSys.privateKey);
            String username = RSAUtils.decryptByPrivateKey(message.username, privateKey);
            Integer id = userMapper.getIdByUsername(username);
            if(id!=null) return SerializeUtil.doSerialize(new ResponseMsg(920,message.dataID));
            String password = RSAUtils.decryptByPrivateKey(message.password, privateKey);
            String flag = RSAUtils.decryptByPrivateKey(message.flag, privateKey);
            userMapper.addUser(username,password,flag);
            LoginUser loginUser = new LoginUser("0", flag);
            String token = TokenUtil.sign(loginUser);
            return SerializeUtil.doSerialize(new ResponseMsg(922,message.dataID,token));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
