package com.csdaa.wsn.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csdaa.wsn.commons.entity.*;
import com.csdaa.wsn.server.config.TempSys;
import com.csdaa.wsn.server.mapper.DepMapper;
import com.csdaa.wsn.server.utils.*;
import it.unisa.dia.gas.jpbc.Element;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

@Service
public class DepService {
    @Autowired
    DepMapper depMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    SysParam sysParam;

    @Autowired
    Contract contract;
    public static String URL="E:\\bishe\\tempc\\";
    public static String prefix=".txt";
    public byte[] uploadData(HttpServletRequest request) throws IOException {
        Message message= null;
        String token = request.getHeader("token");
        String userFlag=TokenUtil.getUflag(token);
        try {
            message = (Message) SerializeUtil.doDeserialize(HttpUtil.getData(request));
            if(message.code==205){
            /*
                存数据库和fabric
             */
                Integer tExist = depMapper.isTExist(message.T);
                System.out.println(tExist);
                if(tExist==0){
//                    FileBlock fileBlock = new FileBlock(0, message.T, message.ck, message.kesi);
//                    Integer bid = depMapper.storeBlockDetail(fileBlock);
                    String key=message.t;
                    String path= TempSys.URL+ SHAutil.getSHA(message.T) +TempSys.prefix;
                    System.out.println(path);
                    File file=new File(path);
                    if(!file.exists()) file.createNewFile();
                    FileOutputStream fos=new FileOutputStream(file);
                    fos.write(Base64.getDecoder().decode(message.c));
                    fos.flush();
                    fos.close();
                    System.out.println(message.index);
                    depMapper.stroeFileDetail(key,message.index,message.T,message.ck,message.kesi);
                    String s = stringRedisTemplate.opsForValue().get(key);
                    if(s==null) stringRedisTemplate.opsForValue().set(key,"1");
                    else {
                        stringRedisTemplate.opsForValue().set(key,Integer.valueOf(s)+1+"");
                    }
                }
                else {
                    depMapper.stroeFileDetail(message.t,message.index,message.T,message.ck,message.kesi);
                }

            }
            if(message.code==207){
            /*
                传送完成，返回存储块数
             */
                depMapper.addOwner(message.t,userFlag);
                String key=message.t;
                String cmd="rsync -avz --delete /usr/data/ rsync_backup@152.136.120.232::backup --password-file=/etc/rsync.password";
                ExecShellUtil.exec(cmd);
                String s = stringRedisTemplate.opsForValue().get(key);
                if(s!=null) stringRedisTemplate.opsForValue().getAndDelete(key);
                System.out.println("s=="+s);

                UUID uuid = UUID.nameUUIDFromBytes((userFlag+new Date().getTime()).getBytes());
                byte[] bytes = SerializeUtil.doSerialize(new ResponseMsg(500, message.dataID, Integer.valueOf(s == null ? "0" : s),uuid.toString()));
                byte[] invokeResult = contract.createTransaction("CreateFileAsset")
                        .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                        .submit(uuid.toString(),message.t,userFlag,"ssp1");
                return bytes;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return SerializeUtil.doSerialize(new ResponseMsg(message.dataID));
    }

    public byte[] checkIsTExist(HttpServletRequest request){
        Message message= null;
        try {
            message= (Message) SerializeUtil.doDeserialize(HttpUtil.getData(request));
            if(message.code==200){
                Integer cnt = depMapper.isFileExist(message.t);
                if(cnt==0){
                    depMapper.insertFile(message.filename,message.t);
                    return SerializeUtil.doSerialize(new ResponseMsg(400,message.dataID));
                }
                else {
                    System.out.println("has existed,will do the proof progress");
                    Random random=new Random();
                    int z=random.nextInt(cnt)+1;
                    Element r1=sysParam.zr.newRandomElement();
                    Element r2=sysParam.zr.newRandomElement();
                    return SerializeUtil.doSerialize(new ResponseMsg(300, message.dataID,z,
                            ElementUtil.e2str(r1),ElementUtil.e2str(r2)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public byte[] verify(HttpServletRequest request){
        Message message= null;
        String token = request.getHeader("token");
        String userFlag=TokenUtil.getUflag(token);
        try {
            message= (Message) SerializeUtil.doDeserialize(HttpUtil.getData(request));
            if(message.code==220){
                Element proof=sysParam.zr.newElementFromBytes(Base64.getDecoder().decode(message.miu));
                Random random1=new Random();
                Random random2=new Random();
                byte[] byteR1 = Base64.getDecoder().decode(message.r1);
                Element r1=sysParam.zr.newElementFromBytes(byteR1);
                byte[] byteR2=Base64.getDecoder().decode(message.r2);
                Element r2=sysParam.zr.newElementFromBytes(byteR2);
                random1.setSeed(r1.toBigInteger().longValue());
                random2.setSeed(r2.toBigInteger().longValue());
                Element leftSum=null;
			    Element rightSum=null;
                for (int i = 1; i <= message.z; i++) {
                    int ai=random1.nextInt(message.z)+1;
                    Element bi=sysParam.zr.newElement(random2.nextInt(message.z)+1);
                    String kesiStr = depMapper.getKesi(message.t, ai-1);
                    Element kesi=sysParam.g1.newElementFromBytes(Base64.getDecoder().decode(kesiStr));
                    String Tstr=depMapper.getTag(message.t,ai-1);
                    Element T=sysParam.zr.newElementFromBytes(Base64.getDecoder().decode(Tstr));
                    byte[] temp=new byte[T.toBytes().length+1];
                    System.arraycopy(T.toBytes(),0,temp,0,T.toBytes().length);
                    temp[temp.length-1]= (byte) message.index;
                    Element M = sysParam.hash2.duplicate().setFromHash(T.toBytes(),0,T.toBytes().length);//bug 改为temp M计算有问题
                    leftSum=(leftSum==null?kesi.duplicate().powZn(bi)
						:leftSum.mul(kesi.duplicate().powZn(bi)));
				    rightSum=(rightSum==null?M.duplicate().powZn(bi)
						:rightSum.mul(M.duplicate().powZn(bi)));
                }
                Element left = sysParam.pr.pairing(leftSum, sysParam.g);
                Element right = sysParam.pr.pairing(rightSum.mul(sysParam.u.duplicate().powZn(proof)),
                        sysParam.g1.newElementFromBytes(Base64.getDecoder().decode(message.t)));
                boolean result=left.isEqual(right);
                if(result){
                    Integer ownerId = depMapper.isOwner(message.t, userFlag);
                    if(ownerId==null) {
                        depMapper.addOwner(message.t, userFlag);
                        UUID uuid = UUID.nameUUIDFromBytes((userFlag+new Date().getTime()).getBytes());
                        byte[] invokeResult = contract.createTransaction("CreateFileAsset")
                                .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                                .submit(uuid.toString(),message.t,userFlag,"ssp1");
                        byte[] bytes = SerializeUtil.doSerialize(new ResponseMsg(516, message.dataID, 0,uuid.toString()));
                        return bytes;
                    }
                    return SerializeUtil.doSerialize(new ResponseMsg(506,message.dataID));
                }
                else {
                    return SerializeUtil.doSerialize(new ResponseMsg(606,message.dataID));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public byte[] download(HttpServletRequest request){
        Message message=null;
        try {
            message=(Message) SerializeUtil.doDeserialize(HttpUtil.getData(request));
            if(message.code==900){
                TagAndCK tagCk = depMapper.getTagCk(message.t, message.index);
                if(tagCk!=null){
                    File f=new File(TempSys.URL+SHAutil.getSHA(tagCk.tag)+TempSys.prefix);
                    RandomAccessFile r=new RandomAccessFile(f,"r");
                    int length= (int) f.length();
                    byte[]win=new byte[length];
                    r.seek(0);
                    r.read(win);
                    r.close();
                    return SerializeUtil.doSerialize(new ResponseMsg(901, message.dataID,
                            Base64.getEncoder().encodeToString(win), tagCk.ck));
                }
                else return SerializeUtil.doSerialize(new ResponseMsg(909, message.dataID,
                        "",""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[]delete(HttpServletRequest request){
        Message message=null;
        String token = request.getHeader("token");
        String userFlag=TokenUtil.getUflag(token);
        try {
            message=(Message)SerializeUtil.doDeserialize(HttpUtil.getData(request));
            String tag=message.t;
            String upid=message.upid;
            byte[] queryOneAsset = contract.createTransaction("ReadFileAsset")
                    .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit(upid);
            JSONObject fileindex = (JSONObject) JSON.parse(queryOneAsset);
            if(fileindex==null||!fileindex.getString("t").equals(tag)||!fileindex.getString("user").equals(userFlag))
                return SerializeUtil.doSerialize(new ResponseMsg(366, message.dataID));
            depMapper.deleteOwner(tag,userFlag);
            Integer integer = depMapper.hasOwner(tag);
            if(integer==0){//没有人再拥有这份文件，可以删除
                Integer fileId = depMapper.getFileId(tag);
                List<Integer> noUseTag = depMapper.getNoUseTag(fileId);
                for(int id:noUseTag){
                    String tag1 = depMapper.getTagById(id);
                    File file=new File(TempSys.URL+SHAutil.getSHA(tag1)+TempSys.prefix);
                    file.delete();
                }
                depMapper.deleteFile(tag);
            }
            String cmd="rsync -avz --delete /usr/data/ rsync_backup@152.136.120.232::backup --password-file=/etc/rsync.password";
            ExecShellUtil.exec(cmd);
            contract.createTransaction("DeleteAsset")
                    .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit(upid);
            return SerializeUtil.doSerialize(new ResponseMsg(801,message.dataID));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
