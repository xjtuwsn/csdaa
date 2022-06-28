package com.csdaa.wsn.client.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csdaa.wsn.client.config.TempSys;
import com.csdaa.wsn.client.utils.SHAutil;
import com.csdaa.wsn.client.utils.SerializeUtil;
import com.csdaa.wsn.commons.entity.*;
import io.netty.handler.codec.http.HttpMethod;
import org.hyperledger.fabric.gateway.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class AuditService {
    @Autowired
    NettyClientService nettyClientService;
    @Autowired
    Contract contract;
    public static String LOG_URL=TempSys.USER_URL+"auditlog";
    public static String WAIT_URL=TempSys.USER_URL+"waitaudit";
    public FileCallBack doAudit(String t,String upid){
        File dir=new File(WAIT_URL);
        if(!dir.exists()) dir.mkdir();
        File wait_file=new File(WAIT_URL+"\\"+ SHAutil.getSHA(t)+"-wait.txt");
        if(wait_file.exists()){
            return new FileCallBack(70,"该文件正在审计中",0,0);
        }
        String di=new Date().getTime()+"";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(new Date());
        Message message = new Message(300, di, t);
        message.setUpid(upid);
        ResponseMsg responseMsg=null;
        try {
            String token="";
            File tokenFile=new File(TempSys.USER_URL+"userconfig\\login_token.txt");
            if (tokenFile.exists()){
                BufferedReader br=new BufferedReader(new FileReader(tokenFile));
                token = br.readLine();
                br.close();
            }
            nettyClientService.sendMsg("/audit/publishChall", SerializeUtil.doSerialize(message), HttpMethod.POST,token);
            wait_file.createNewFile();
            FileWriter fw=new FileWriter(wait_file,true);
            fw.write(format+"\n");
            fw.flush();
            fw.write(t);
            fw.flush();
            fw.close();
//            File file=new File(LOG_URL+"\\"+ SHAutil.getSHA(t)+"-log.txt");
//            if(!file.exists()) file.createNewFile();
//            FileWriter fw=new FileWriter(file,true);
//            fw.write(format+"\n");
//            fw.flush();
//            for (int i = 0; i < responseMsg.ids.size(); i++) {
//                if(i==responseMsg.ids.size()-1) fw.write(responseMsg.ids.get(i)+"\n");
//                else fw.write(responseMsg.ids.get(i)+",");
//                fw.flush();
//            }
            fw.close();

            return new FileCallBack(6,"正在审计，请稍后查看记录",0,0);
                //result=0

        } catch (IOException e) {
            e.printStackTrace();
            return new FileCallBack(7,"服务器出错",0,0);

        }
    }
    public FileCallBack doBatchAudit(String []batchT,String []batchUpid){
        File dir=new File(WAIT_URL);
        if(!dir.exists()) dir.mkdir();
        String t="";
        for (int i = 0; i < batchT.length; i++) {
            t+=batchT[i];
            if(i!=batchT.length-1) t+=",";
        }
        File wait_file=new File(WAIT_URL+"\\"+ SHAutil.getSHA(t)+"-wait.txt");
        if(wait_file.exists()){
            return new FileCallBack(70,"文件正在审计中",0,0);
        }
        String di=new Date().getTime()+"";
        BatchAudit batchAudit=new BatchAudit(450,di, Arrays.asList(batchT),Arrays.asList(batchUpid));
        ResponseMsg responseMsg=null;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(new Date());
        try {
            String token="";
            File tokenFile=new File(TempSys.USER_URL+"userconfig\\login_token.txt");
            if (tokenFile.exists()){
                BufferedReader br=new BufferedReader(new FileReader(tokenFile));
                token = br.readLine();
                br.close();
            }
            nettyClientService.sendMsg("/audit/batchPublish", SerializeUtil.doSerialize(batchAudit), HttpMethod.POST,token);
            wait_file.createNewFile();
            FileWriter fw=new FileWriter(wait_file,true);
            fw.write(format+"\n");
            fw.flush();
            for(String b:batchT){
                fw.write(b+"\n");
                fw.flush();
            }
            fw.close();
//            for(String t:batchT){
//                File file=new File(LOG_URL+"\\"+ SHAutil.getSHA(t)+"-log.txt");
//                if(!file.exists()) file.createNewFile();
//                FileWriter fw=new FileWriter(file,true);
//                fw.write(format+"\n");
//                fw.flush();
//                for (int i = 0; i < responseMsg.ids.size(); i++) {
//                    if(i==responseMsg.ids.size()-1) fw.write(responseMsg.ids.get(i)+"\n");
//                    else fw.write(responseMsg.ids.get(i)+",");
//                    fw.flush();
//                }
//                fw.close();
//            }

            return new FileCallBack(8,"正在进行批量审计，请稍后在记录中查看",0,0);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public List<AuditLogs> getLogs(String t){
        File file=new File(LOG_URL+"\\"+ SHAutil.getSHA(t)+"-log.txt");
        if(!file.exists()) return null;
        try {
            BufferedReader br=new BufferedReader(new FileReader(file));
            String date=null;
            List<AuditLogs> list=new ArrayList<>();
            while ((date=br.readLine())!=null){
                String idstr=br.readLine();
                String []ids=idstr.split(",");
                byte[] queryOneAsset = contract.createTransaction("ReadAuditAsset")
                        .submit(ids[2]);
                JSONObject parse = (JSONObject) JSON.parse(queryOneAsset);
                AuditLogs auditLogs = new AuditLogs(parse.getString("id"), date, parse.getString("t"),
                        parse.getString("auditor"), parse.getString("auditee"), parse.getString("z"),
                        parse.getString("r1"), parse.getString("r2"), parse.getString("proof"),
                        parse.getInteger("result"), parse.getString("sigma"));
                list.add(auditLogs);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public FileCallBack isAuditFin(){
        File dir=new File(WAIT_URL);
        if(!dir.exists()) {
            dir.mkdir();
            return new FileCallBack(1,"0",0,0);
        }
        File[] files = dir.listFiles();
        if(files==null||files.length==0){
            return new FileCallBack(1,"0",0,0);
        }
        try {
            String token="";
            File tokenFile=new File(TempSys.USER_URL+"userconfig\\login_token.txt");
            if (tokenFile.exists()){
                BufferedReader br=new BufferedReader(new FileReader(tokenFile));
                token = br.readLine();
                br.close();
            }
            boolean fin=false;
            for(File file:files){
                String name=file.getName();
                String t=name.split("-")[0];
                ResponseMsg responseMsg=null;
                String di=new Date().getTime()+"";
                Message message = new Message(300, di, t);
                responseMsg = nettyClientService.sendSyncMsg("/audit/isfin", SerializeUtil.doSerialize(message), HttpMethod.POST, di,token);
                if(responseMsg.code==1024){
                    fin=true;
                    BufferedReader br=new BufferedReader(new FileReader(file));
                    String formate=br.readLine();
                    System.out.println(formate);
                    String date=null;
                    while ((date=br.readLine())!=null){
                        String idstr=date;
                        System.out.println(idstr);
                        File audit_file=new File(LOG_URL+"\\"+ SHAutil.getSHA(idstr)+"-log.txt");
                        if(!audit_file.exists()) audit_file.createNewFile();
                        FileWriter fw=new FileWriter(audit_file,true);
                        fw.write(formate+"\n");
                        fw.flush();
                        for (int i = 0; i < responseMsg.ids.size(); i++) {
                            if(i==responseMsg.ids.size()-1) fw.write(responseMsg.ids.get(i)+"\n");
                            else fw.write(responseMsg.ids.get(i)+",");
                            fw.flush();
                        }
                    }
                    br.close();
                    file.delete();
                }
            }
            if(fin) return new FileCallBack(2,"0",0,0);

        }catch (IOException e){
            e.printStackTrace();
        }
        return new FileCallBack(1,"0",0,0);
    }
}
