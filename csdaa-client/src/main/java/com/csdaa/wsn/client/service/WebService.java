package com.csdaa.wsn.client.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csdaa.wsn.client.config.TempSys;
import com.csdaa.wsn.client.utils.EncryptUtil;
import com.csdaa.wsn.client.utils.RSAUtils;
import com.csdaa.wsn.client.utils.SerializeUtil;
import com.csdaa.wsn.commons.entity.LocalFile;
import com.csdaa.wsn.commons.entity.LoginMessage;
import com.csdaa.wsn.commons.entity.ResponseMsg;
import io.netty.handler.codec.http.HttpMethod;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.json.Json;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class WebService {
    @Autowired
    Contract contract;
    @Autowired
    NettyClientService nettyClientService;
    public static String USER_URL=TempSys.USER_URL+"userinfo";
    public List<LocalFile> getAllFiles(){
        File file=new File(USER_URL);
        List<LocalFile> res=new ArrayList<>();
        if(!file.exists()) return res;
        File[] files = file.listFiles();
        if(files==null) return res;
        for(File f:files){
            try {
                BufferedReader br=new BufferedReader(new FileReader(f));
                String s = br.readLine();
                String[] split = s.split(":--:");
                res.add(new LocalFile(split[0],split[1],split[2],split[3],split[4],f.getName()));
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return res;
    }
    public JSONObject query(String id){
        if(id.indexOf('-')==-1){
            try {
                byte[] submit = contract.createTransaction("ReadAuditAsset")
                        .submit(id);
                return (JSONObject)JSON.parse(submit);
            } catch (ContractException | TimeoutException | InterruptedException e) {
                return null;
            }
        }
        try {
            byte[] submit = contract.createTransaction("ReadFileAsset")
                    .submit(id);
            return (JSONObject)JSON.parse(submit);
        } catch (ContractException | TimeoutException | InterruptedException e) {
            return null;
        }
    }
    public boolean isLogin(){
        File file=new File(TempSys.USER_URL+"userconfig\\login_token.txt");
        String di=new Date().getTime()+"";
        if(!file.exists()) return false;
        try {
            BufferedReader br=new BufferedReader(new FileReader(file));
            String token=br.readLine();
            br.close();
            LoginMessage message = new LoginMessage(di, "", "", token,"");
            ResponseMsg responseMsg = nettyClientService.sendSyncMsg("/user/islogin", SerializeUtil.doSerialize(message), HttpMethod.POST, di,token);
            return responseMsg.code == 976;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public String login(String username,String password){
        File file=new File(TempSys.USER_URL+"sspconfig\\pk.txt");
        if(!file.exists()) return "";
        try {
            BufferedReader br=new BufferedReader(new FileReader(file));
            String publicKeyStr=br.readLine();
            br.close();
            RSAPublicKey publicKey = RSAUtils.getPublicKey(publicKeyStr);
            String encUsername = RSAUtils.encryptByPublicKey(username, publicKey);
            String encPassword = RSAUtils.encryptByPublicKey(password, publicKey);
            String dataID=new Date().getTime()+"";
            LoginMessage loginMessage = new LoginMessage(dataID, encUsername, encPassword,"","");
            ResponseMsg responseMsg = nettyClientService.sendSyncMsg("/user/login", SerializeUtil.doSerialize(loginMessage), HttpMethod.POST, dataID,"");
            if(responseMsg.code==987){
                String token=responseMsg.token;
                File f=new File(TempSys.USER_URL+"userconfig\\login_token.txt");
                if(!file.exists()) f.createNewFile();
                FileWriter fw=new FileWriter(f,false);
                fw.write(token);
                fw.flush();
                fw.close();
                return token;
            }
            else return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public String register(String username,String password,String flag){
        File file=new File(TempSys.USER_URL+"userconfig\\user_sk.txt");
        File fpk=new File(TempSys.USER_URL+"sspconfig\\pk.txt");
        if(!file.exists()||!fpk.exists()) return "";
        BufferedReader br= null;
        BufferedReader pkr= null;
        try {
            br = new BufferedReader(new FileReader(file));
            String sk=br.readLine();
            br.close();
            pkr=new BufferedReader(new FileReader(fpk));
            String sspPK=pkr.readLine();
            pkr.close();
            byte[] aesFlag = EncryptUtil.encrypt(flag.getBytes(StandardCharsets.UTF_8), sk.getBytes(StandardCharsets.UTF_8));
            RSAPublicKey publicKey = RSAUtils.getPublicKey(sspPK);
            String encUsername = RSAUtils.encryptByPublicKey(username, publicKey);
            String encPassword = RSAUtils.encryptByPublicKey(password, publicKey);
            String encFlag = RSAUtils.encryptByPublicKey(Base64.getEncoder().encodeToString(aesFlag), publicKey);
            System.out.println(Base64.getEncoder().encodeToString(aesFlag));
            String dataID=new Date().getTime()+"";
            LoginMessage message = new LoginMessage(dataID, encUsername, encPassword, "", encFlag);
                ResponseMsg responseMsg = nettyClientService.sendSyncMsg("/user/register", SerializeUtil.doSerialize(message), HttpMethod.POST, dataID,"");
            if(responseMsg.code==922){
                String token=responseMsg.token;
                File f=new File(TempSys.USER_URL+"userconfig\\login_token.txt");
                File uf=new File(TempSys.USER_URL+"userconfig\\user_flag.txt");
                if(!f.exists()) f.createNewFile();
                if(!uf.exists()) uf.createNewFile();
                FileWriter fw=new FileWriter(f,false);
                FileWriter fu=new FileWriter(uf,true);
                fw.write(token);
                fw.flush();
                fw.close();
                fu.write(flag+"\n");
                fu.write(Base64.getEncoder().encodeToString(aesFlag));
                fu.flush();
                fu.close();
                return token;
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
