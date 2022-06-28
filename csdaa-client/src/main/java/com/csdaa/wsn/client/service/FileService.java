package com.csdaa.wsn.client.service;

import com.csdaa.wsn.client.config.TempSys;
import com.csdaa.wsn.commons.entity.FileCallBack;
import com.csdaa.wsn.commons.entity.Message;
import com.csdaa.wsn.commons.entity.ResponseMsg;
import com.csdaa.wsn.commons.entity.SysParam;
import com.csdaa.wsn.client.utils.*;
import io.netty.handler.codec.http.HttpMethod;
import it.unisa.dia.gas.jpbc.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class FileService {
    public static Integer divisor = 127;
    public static Integer remainder = 3;
    public static int WIN_SIZE = 64;
    public static Long MAX_SIZE = (long) (100 * 1024);
    public static Long MIN_SIZE = (long) (80 * 1024);
    public static String DEFAULT_DOWN_URL=TempSys.USER_URL+"download\\";
    public static Long allTime= 0L;
    public static Long allSize=0L;
    @Autowired
    NettyClientService nettyClientService;
    @Autowired
    SysParam sysParam;
    public FileCallBack upload(MultipartFile mfile) throws IOException, NoSuchAlgorithmException {
        allTime=0L;
        allSize=0L;
        String filename=mfile.getOriginalFilename();
        File file=File.createTempFile(filename.split("\\.")[0]+"asasas","."+filename.split("\\.")[1]);
        mfile.transferTo(file);
        long beforeSize = file.length();
        String fileSHA = SHAutil.getFileSHA(file);
        byte[] fileByte = fileSHA.getBytes();
        sysParam.sk=sysParam.hash1.duplicate().setFromHash(fileByte,0,fileByte.length);
        sysParam.t=sysParam.g.duplicate().powZn(sysParam.sk);
        String token="";
        File tokenFile=new File(TempSys.USER_URL+"userconfig\\login_token.txt");
        if (tokenFile.exists()){
            BufferedReader br=new BufferedReader(new FileReader(tokenFile));
            token = br.readLine();
            br.close();
        }
        System.out.println("==========");
        System.out.println(ElementUtil.e2str(sysParam.sk));
        System.out.println("============");
        ResponseMsg res = isExist(sysParam.t, filename,token);
        List<Element> list=new ArrayList<>();
        System.out.println(res);
       // return;

        long fileLength = file.length();
        int threadNum = 1;
        // 对文件进行切分,partSize表示每部分的大小，r表示取模后的余数
        long partSize = fileLength / threadNum;
        long r = (int) (fileLength % partSize);
        // t1 记录时间
        long t1 = System.currentTimeMillis();
        ThreadPoolExecutor threadPool = null;
        // 判断每部分的大小，进行切分
        // 如果每个线程分到的容量大于128*MAX_SIZE，则每个线程每次处理大小为128*MAX_SIZE
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        long start=0;
        long end=start+partSize;
        int index=0;

        // [奇数]记录分块数和[偶数]实际写入的文件数
        int[] record = new int[2*index+2];
        long startTime = System.currentTimeMillis();
        try {
            // 得到md5加密实例
            // 得到数据库键值 如file-1231abcdef-0
            // secretCodes有序地存储数据指纹，用于恢复文件
            ArrayList<String> secretCodes = new ArrayList<String>();
            // 得到要拆分文件的访问流
            RandomAccessFile rFile = new RandomAccessFile(file, "r");
            // 滑动窗口
            byte[] win = new byte[WIN_SIZE];
            // 从最小长度开始循环读取
            long now = MIN_SIZE + start;
            rFile.seek(now);
            rFile.read(win);
            BigInteger temp = null;
            int block=0;

            while (now < end) {
                // 如果匹配到或者达到了上限要求，就开始计算
                if (now - start >= MAX_SIZE || (temp = SecretCodeUtil.md5Int(win, md5))
                        .mod(new BigInteger(divisor + "")).equals(new BigInteger(remainder + ""))) {
                    if (write(start, now - start, rFile, secretCodes, md5,sysParam,block,filename,res,list,token))
                        record[2 * index]++;
                    block++;
                    record[2 * index + 1]++;
                    // 写入完成后重设start和now
                    start = now;
                    now = start + MIN_SIZE;
                }
                // 否则，窗口继续向前移动一个字节
                else
                    now++;
                rFile.seek(now);
                rFile.read(win);
            }
            // 当达到了文件的最大长度，进行写入
            if (now >= end) {
                if (write(start, end - start, rFile, secretCodes, md5,sysParam,block,filename,res,list,token))
                    record[2 * index]++;
                record[2 * index + 1]++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        long endFirstTime=System.currentTimeMillis();
        System.out.println("第一次上传的时间："+allTime);
        System.out.println("原始文件大小："+beforeSize);
        System.out.println("实际上传大小："+allSize);
        int writeNum = 0;
        int allNum = 0;
        for (int i = 0; i < record.length; i++) {
            if (i % 2 == 0)
                writeNum += record[i];
            else
                allNum += record[i];
        }
        String di=new Date().getTime()+"";
        if(res.code==300){
            Element proof=null;
            Random random1=new Random();
            Random random2=new Random();
            byte[] byteR1 = Base64.getDecoder().decode(res.r1);
            Element r1=sysParam.zr.newElementFromBytes(byteR1);
            byte[] byteR2=Base64.getDecoder().decode(res.r2);
            Element r2=sysParam.zr.newElementFromBytes(byteR2);
            random1.setSeed(r1.toBigInteger().longValue());
            random2.setSeed(r2.toBigInteger().longValue());
            for (int i = 1; i <= res.z; i++) {

                int ai=random1.nextInt(res.z)+1;

                Element bi=sysParam.zr.newElement(random2.nextInt(res.z)+1);
                proof=(proof == null?bi.duplicate().mul(list.get(ai-1))
					:proof.add(bi.duplicate().mul(list.get(ai-1))));
            }
            long secondTime=System.currentTimeMillis();
            System.out.println("第二次上传的时间："+(secondTime-startTime));
            ResponseMsg responseMsg = nettyClientService.sendSyncMsg("/dep/verify",
                    SerializeUtil.doSerialize(new Message(220, di, ElementUtil.e2str(sysParam.t),
                            ElementUtil.e2str(proof), res.z, res.r1, res.r2)),
                    HttpMethod.POST, di,token);
            if(responseMsg.code==506){
                System.out.println("所有权认证成功");
                //success
                return new FileCallBack(1,"文件已存在，所有权认证成功",0,0);

            }
            if(responseMsg.code==516){
                System.out.println("所有权认证成功");
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String uptime = sdf.format(new Date());
                String data=ElementUtil.e2str(sysParam.t)+":--:"+ElementUtil.e2str(sysParam.sk)+":--:"+filename+":--:"+uptime+":--:"+responseMsg.upid;
                File ff=new File("E:\\bishe\\userinfo");
                if(!ff.exists()) ff.mkdir();
                File f=new File("E:\\bishe\\userinfo\\"+new Date().getTime()+".txt");
                f.createNewFile();
                FileOutputStream fso=new FileOutputStream(f);
                fso.write(data.getBytes());
                fso.flush();
                fso.close();
                //success
                return new FileCallBack(1,"文件已存在，所有权认证成功",0,0);

            }
            if(responseMsg.code==606){
                System.out.println("所有权认证失败");
                return new FileCallBack(2,"所有权认证失败",0,0);
                //failed

            }
        }


        System.out.println("+++++++ssssssssssssssssssssssssssss+");
        ResponseMsg responseMsg = nettyClientService.sendSyncMsg("/dep/metedata",
                SerializeUtil.doSerialize(new Message(207, di,ElementUtil.e2str(sysParam.t))),
                HttpMethod.POST, di,token);

        System.out.println(responseMsg);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String uptime = sdf.format(new Date());
        String data=ElementUtil.e2str(sysParam.t)+":--:"+ElementUtil.e2str(sysParam.sk)+":--:"+filename+":--:"+uptime+":--:"+responseMsg.upid;
        File ff=new File("E:\\bishe\\userinfo");
        if(!ff.exists()) ff.mkdir();
        File f=new File("E:\\bishe\\userinfo\\"+new Date().getTime()+".txt");
        f.createNewFile();
        FileOutputStream fso=new FileOutputStream(f);
        fso.write(data.getBytes());
        fso.flush();
        fso.close();
        int factNum=responseMsg.recordNum;

        System.out.println("=============factWriteNum:"+factNum);
        return new FileCallBack(0,"文件上传成功",factNum,allNum);
    }
    public ResponseMsg isExist(Element t,String filename,String token){
        String s = ElementUtil.e2str(t);
        System.out.println(s);
        String dataID=new Date().getTime()+"";
        ResponseMsg responseMsg = null;
        try {
            responseMsg = nettyClientService.sendSyncMsg("/dep/checkTexist",
            SerializeUtil.doSerialize(new Message(200,dataID,s,filename)),
            HttpMethod.POST,
            dataID,token);
            return responseMsg;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean write(long start, long writeLen, RandomAccessFile rFile, ArrayList<String> secretCodes,
                                MessageDigest md5, SysParam sysParam, int block,String filename,
                         ResponseMsg res,List<Element> list,String token) throws Exception {
        long beginTime=System.currentTimeMillis();
        // result用于记录是否文件
        boolean result = false;
        // buf用于写入数据
        byte[] buf = new byte[(int) writeLen];
        rFile.seek(start);
        rFile.read(buf);

        Element k = sysParam.hash1.duplicate().setFromHash(buf, 0, buf.length);
        byte[] c = EncryptUtil.encrypt(buf, k.toBytes());
        String sha = SHAutil.getSHA(Base64.getEncoder().encodeToString(c));
        byte[] bufBytes = sha.getBytes();
        Element T = sysParam.hash1.duplicate().setFromHash(bufBytes, 0, bufBytes.length);
        if(res.code==300){
            list.add(T);
            return false;

        }
        byte[] temp=new byte[T.toBytes().length+1];
        System.arraycopy(T.toBytes(),0,temp,0,T.toBytes().length);
        temp[temp.length-1]= (byte) block;
        Element M = sysParam.hash2.duplicate().setFromHash(T.toBytes(),0,T.toBytes().length);
        Element e1 = sysParam.u.duplicate().powZn(T);
        Element e2 = M.duplicate().mul(e1);
        Element kesi = e2.powZn(sysParam.sk);
        byte[] ck = EncryptUtil.encrypt(k.toBytes(), ElementUtil.e2str(sysParam.sk).getBytes());
        allSize=allSize+c.length+T.toBytes().length+ElementUtil.e2str(kesi).getBytes().length+sysParam.t.toBytes().length;
        long endTime=System.currentTimeMillis();
        allTime+=(endTime-beginTime);
        Message message = new Message(205, "", ElementUtil.e2str(sysParam.t), ElementUtil.e2str(T),
                Base64.getEncoder().encodeToString(c),
                Base64.getEncoder().encodeToString(ck),
                ElementUtil.e2str(kesi), "",block,filename);

        byte[] data = SerializeUtil.doSerialize(message);
        nettyClientService.sendMsg("/dep/metedata",data,HttpMethod.POST,token);
        //Jedis redis = RedisUtil.getRedisFromPool();
        //String tag=ElementUtil.e2str(T);
//        ks.put(tag,k);
//        kList.add(k);
//        kesiList.add(kesi);
//        mList.add(M);
//        TList.add(T);
        // 若存在，则不用写
        //String value = redis.get(tag);
        //redis.rpush(ElementUtil.e2str(sysParam.t),tag);
//        if (value != null){
//        }
        // 若不存在，则必须写
//        else {
//            redis.set(tag, Base64.getEncoder().encodeToString(c));
//            result = true;
//        }
//        redis.close();
        return result;
    }

    public FileCallBack download(String t,String filename,String sk) throws Exception {
        int block=0;
        ResponseMsg responseMsg=null;
        File file=new File(DEFAULT_DOWN_URL+filename);
        if(!file.exists()) file.createNewFile();
        String token="";
        File tokenFile=new File(TempSys.USER_URL+"userconfig\\login_token.txt");
        if (tokenFile.exists()){
            BufferedReader br=new BufferedReader(new FileReader(tokenFile));
            token = br.readLine();
            br.close();
        }
        FileOutputStream fos=new FileOutputStream(file,true);
        do {
            String di=new Date().getTime()+"";
            Message message = new Message(900, di, t, block);
            responseMsg = nettyClientService.sendSyncMsg("/dep/download", SerializeUtil.doSerialize(message), HttpMethod.POST, di,token);
            if(responseMsg.code==909)break;
            System.out.println(block);
            System.out.println(responseMsg.ck);
            byte[] k = EncryptUtil.decrypt(Base64.getDecoder().decode(responseMsg.ck), sk.getBytes());
            byte[] data = EncryptUtil.decrypt(Base64.getDecoder().decode(responseMsg.c), k);
            fos.write(data);
            fos.flush();
            block++;
        }while (responseMsg.code!=909);
        fos.close();
        return new FileCallBack(3,DEFAULT_DOWN_URL+filename,0,0);
    }

    public FileCallBack delete(String t,String localName,String upid) {
        ResponseMsg responseMsg=null;
        String di=new Date().getTime()+"";
        try {
            String token="";
            File tokenFile=new File(TempSys.USER_URL+"userconfig\\login_token.txt");
            if (tokenFile.exists()){
                BufferedReader br=new BufferedReader(new FileReader(tokenFile));
                token = br.readLine();
                br.close();
            }
            Message message = new Message(800, di, t);
            message.setUpid(upid);
            responseMsg = nettyClientService.sendSyncMsg("/dep/delete", SerializeUtil.doSerialize(message), HttpMethod.POST, di,token);
            if(responseMsg.code==366) return new FileCallBack(5,"",0,0);
            if(responseMsg.code==801) {
                File file=new File(TempSys.USER_URL+"userinfo\\"+localName);
                File auFile=new File(TempSys.USER_URL+"auditlog\\"+SHAutil.getSHA(t)+"-log.txt");
                auFile.delete();
                file.delete();
                return new FileCallBack(4, "", 0, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
