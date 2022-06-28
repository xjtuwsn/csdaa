package com.csdaa.wsn.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csdaa.wsn.commons.entity.*;
import com.csdaa.wsn.server.callable.ProofCallable;
import com.csdaa.wsn.server.callable.PublichCallable;
import com.csdaa.wsn.server.callable.VerifyCallable;
import com.csdaa.wsn.server.config.TempSys;
import com.csdaa.wsn.server.mapper.DepMapper;
import com.csdaa.wsn.server.utils.*;
import it.unisa.dia.gas.jpbc.Element;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.concurrent.*;

@Service
public class AuditService {
    @Autowired
    Contract contract;
    @Autowired
    DepMapper depMapper;
    @Autowired
    SysParam sysParam;
    @Autowired
    ExecutorService executorService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    HttpClient httpClient;
    public static String URL="E:\\bishe\\tempc\\";
    public static String prefix=".txt";
    public byte[] publishChall(HttpServletRequest request){
        Message message=null;
        String token = request.getHeader("token");
        String userFlag=TokenUtil.getUflag(token);
        try {
            message= (Message) SerializeUtil.doDeserialize(HttpUtil.getData(request));
            String t=message.t;
            String upid=message.upid;

            byte[] queryOneAsset = contract.createTransaction("ReadFileAsset")
                    .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit(upid);
            JSONObject fileindex = (JSONObject)JSON.parse(queryOneAsset);
            if(!fileindex.getString("t").equals(t)||!fileindex.getString("user").equals(userFlag))
                return SerializeUtil.doSerialize(new ResponseMsg(366, message.dataID));
            Integer cnt = depMapper.isFileExist(message.t);
            Random random=new Random();
            int z=random.nextInt(cnt)+1;
            Element r1=sysParam.zr.newRandomElement();
            Element r2=sysParam.zr.newRandomElement();
            String id=SHAutil.getSHA(t+new Date().getTime());
            String auditor="SSP1",auditee="SSP2",logType="challenge";
            byte[] invokeResult = contract.createTransaction("CreateAuditAsset")
                    .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit(id,t,auditor,auditee,logType,z+"", ElementUtil.e2str(r1),ElementUtil.e2str(r2),"","2","","");
            JSONObject parse =(JSONObject) JSON.parse(invokeResult);
            RSAPublicKey publicKey = RSAUtils.getPublicKey(TempSys.publicKey);
            String encFlag=RSAUtils.encryptByPublicKey(userFlag,publicKey);
            String qid=parse.getString("id");
            MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
            params.add("qid",qid);
            params.add("userFlag",encFlag);
            String auditKey=SHAutil.getSHA(parse.getString("t"))+"-"+userFlag;
            stringRedisTemplate.opsForList().rightPush(auditKey,qid);
            System.out.println(auditKey);
            System.out.println(qid);
            httpClient.call("/audit/doproof", HttpMethod.POST,params);
//            String s = doProof(message, qid);
//            String[] split = s.split(":");
//            String result=split[0];
//            List<String> ans=new ArrayList<>();
//            ans.add(qid);ans.add(split[2]);ans.add(split[1]);
            ResponseMsg responseMsg = new ResponseMsg(388, message.dataID);
            //responseMsg.ids=ans;

            return SerializeUtil.doSerialize(responseMsg);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String doProof(HttpServletRequest request) {

        try {
            String qid = request.getParameter("qid");

            byte[] queryOneAsset = contract.createTransaction("ReadAuditAsset")
                    .submit(qid);
            JSONObject parse = (JSONObject) JSON.parse(queryOneAsset);
            String id=SHAutil.getSHA(parse.getString("t")+new Date().getTime());
            String auditor=parse.getString("auditor");
            String auditee=parse.getString("auditee");
            String logType="proof";
            int z = parse.getInteger("z");
            Element r1 = sysParam.zr.newElementFromBytes(Base64.getDecoder().decode(parse.getString("r1")));
            Element r2 = sysParam.zr.newElementFromBytes(Base64.getDecoder().decode(parse.getString("r2")));
            Element proof=null;
            Random random1=new Random();
            Random random2=new Random();
            random1.setSeed(r1.toBigInteger().longValue());
            random2.setSeed(r2.toBigInteger().longValue());
            Set<String> set = stringRedisTemplate.opsForSet().members(parse.getString("t") + "audit");
            for(int i=1;i<=z;i++){

                int ai=random1.nextInt(z)+1;
                if(set!=null&&set.contains(ai+"")){
                    continue;
                }

                Element bi=sysParam.zr.newElement(random2.nextInt(z)+1);
                String tag = depMapper.getTag(parse.getString("t"), ai-1);
                File f=new File(TempSys.URL+ SHAutil.getSHA(tag)+TempSys.prefix);
                RandomAccessFile r=new RandomAccessFile(f,"r");
                int length= (int) f.length();
                byte[]c=new byte[length];
                r.seek(0);
                r.read(c);
                r.close();
                String sha = SHAutil.getSHA(Base64.getEncoder().encodeToString(c));
                byte[] bufBytes = sha.getBytes();
                Element T = sysParam.hash1.duplicate().setFromHash(bufBytes, 0, bufBytes.length);
                proof=(proof == null?bi.duplicate().mul(T)
                        :proof.add(bi.duplicate().mul(T)));
            }
            byte[] invokeResult = contract.createTransaction("CreateAuditAsset")
                    .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit(id,parse.getString("t"),auditor,auditee,logType,z+"",
                            ElementUtil.e2str(r1),ElementUtil.e2str(r2),ElementUtil.e2str(proof),"2","","");
            JSONObject res =(JSONObject) JSON.parse(invokeResult);
            MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
            params.add("qid",res.getString("id"));
            params.add("userFlag",request.getParameter("userFlag"));
            System.out.println(qid);
            httpClient.call("/audit/doverify", HttpMethod.POST,params);
            //return doVerify(message, res.getString("id"))+":"+res.getString("id");
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public String doVerify(HttpServletRequest request) {

        try {
            long l = System.currentTimeMillis();
            String qid=request.getParameter("qid");
            String encFlag = request.getParameter("userFlag");
            RSAPrivateKey privateKey = RSAUtils.getPrivateKey(TempSys.privateKey);
            String userFlag= RSAUtils.decryptByPrivateKey(encFlag,privateKey);
            String logType="result";

            byte[] queryOneAsset = contract.createTransaction("ReadAuditAsset")
                    .submit(qid);
            JSONObject parse = (JSONObject) JSON.parse(queryOneAsset);
            String id = SHAutil.getSHA(parse.getString("t")+new Date().getTime());
            int z=parse.getInteger("z");
            String auditor=parse.getString("auditor");
            String auditee=parse.getString("auditee");
            Element r1 = sysParam.zr.newElementFromBytes(Base64.getDecoder().decode(parse.getString("r1")));
            Element r2 = sysParam.zr.newElementFromBytes(Base64.getDecoder().decode(parse.getString("r2")));
            Element proof=sysParam.zr.newElementFromBytes(Base64.getDecoder().decode(parse.getString("proof")));
            Random random1=new Random();
            Random random2=new Random();
            random1.setSeed(r1.toBigInteger().longValue());
            random2.setSeed(r2.toBigInteger().longValue());
            Element leftSum=null;
            Element rightSum=null;
            String key=parse.getString("t") + "audit";
            Set<String> set = stringRedisTemplate.opsForSet().members(key);
            List<String> list=new ArrayList<>();
            int cnt=0;
            for(int i=1;i<=z;i++){

                int ai=random1.nextInt(z)+1;
                if(set!=null&&set.contains(ai+"")) continue;
                list.add(ai+"");
                cnt++;

                Element bi=sysParam.zr.newElement(random2.nextInt(z)+1);
                String kesiStr = depMapper.getKesi(parse.getString("t"), ai-1);
                Element kesi=sysParam.g1.newElementFromBytes(Base64.getDecoder().decode(kesiStr));
                String Tstr=depMapper.getTag(parse.getString("t"),ai-1);
                Element T=sysParam.zr.newElementFromBytes(Base64.getDecoder().decode(Tstr));
                byte[] temp=new byte[T.toBytes().length+1];
                System.arraycopy(T.toBytes(),0,temp,0,T.toBytes().length);
                temp[temp.length-1]= (byte) (ai-1);
                Element M = sysParam.hash2.duplicate().setFromHash(T.toBytes(),0,T.toBytes().length);//bug
                leftSum=(leftSum==null?kesi.duplicate().powZn(bi)
                        :leftSum.mul(kesi.duplicate().powZn(bi)));
                rightSum=(rightSum==null?M.duplicate().powZn(bi)
                        :rightSum.mul(M.duplicate().powZn(bi)));
            }
            System.out.println(set.size());
            for(String s:list) System.out.println(s);
            String[] strings = list.toArray(new String[0]);
            stringRedisTemplate.opsForSet().add(key,strings);
            if(set.size()==0) stringRedisTemplate.expire(key,2, TimeUnit.HOURS);
            Element left = sysParam.pr.pairing(leftSum, sysParam.g);
            Element right = sysParam.pr.pairing(rightSum.duplicate().mul(sysParam.u.duplicate().powZn(proof)),
                    sysParam.g1.newElementFromBytes(Base64.getDecoder().decode(parse.getString("t"))));
            String result="0";
            if(left.isEqual(right)) result="1";
            byte[] invokeResult = contract.createTransaction("CreateAuditAsset")
                    .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit(id,parse.getString("t"),auditor,auditee,logType,z+"", ElementUtil.e2str(r1),
                            ElementUtil.e2str(r2),ElementUtil.e2str(proof),result,ElementUtil.e2str(leftSum),ElementUtil.e2str(rightSum));
            String auditKey=SHAutil.getSHA(parse.getString("t"))+"-"+userFlag;
            JSONObject res =(JSONObject) JSON.parse(invokeResult);
            long l1 = System.currentTimeMillis();
            stringRedisTemplate.opsForList().rightPush(auditKey,qid);
            stringRedisTemplate.opsForList().rightPush(auditKey,res.getString("id"));
            System.out.println(auditKey);
            System.out.println(res.getString("id"));
            System.out.println("单个审计的verify时间:"+(l1-l)+",总次数为："+cnt+",z="+z);
            return result+":"+res.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public byte[] doBatchPublishChallen(HttpServletRequest request){
        BatchAudit batchAudit=null;
        System.out.println("1111");
        String token = request.getHeader("token");
        String userFlag=TokenUtil.getUflag(token);
        try {
            String auditor="SSP1",auditee="SSP2",logType="batch_chall";
            batchAudit=(BatchAudit)SerializeUtil.doDeserialize(HttpUtil.getData(request));
            List<String> ts = batchAudit.getTs();
            List<String> upids = batchAudit.getUpids();
            List<BatchAuditParam> paras=new ArrayList<>();
            List<Future<BatchAuditParam>> list=new ArrayList<>();
            int len=ts.size();
            for (int i = 0; i < len; i++) {
                Future<BatchAuditParam> future = executorService.submit(new PublichCallable(ts.get(i), upids.get(i), auditor, auditee,
                        sysParam, depMapper, contract, userFlag,i));
                list.add(future);
            }
            for (int i = 0; i < len; i++) {
                Future<BatchAuditParam> future = list.get(i);
                while (!future.isDone()){}
                BatchAuditParam s = future.get();
                if(s!=null) paras.add(s);
            }
            System.out.println("chanllen阶段=======");
            paras.sort((m,n)->{
                return m.index-n.index;
            });
            String z="",r1="",r2="",clustert="";
            for (int i = 0; i < paras.size(); i++) {
                BatchAuditParam b = paras.get(i);
                z+=b.z;r1+=b.r1;r2+=b.r2;clustert+=ts.get(b.index);
                if(i!=paras.size()-1){
                    z+=",";r1+=",";r2+=",";clustert+=",";
                }
            }
            String id= SHAutil.getSHA(UUID.randomUUID().toString()+new Date().getTime());
            byte[] invokeResult = contract.createTransaction("CreateAuditAsset")
               .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                .submit(id,clustert,auditor,auditee,logType,z, r1,r2,"","2","","");
            JSONObject parse =(JSONObject) JSON.parse(invokeResult);

            RSAPublicKey publicKey = RSAUtils.getPublicKey(TempSys.publicKey);
            String encFlag=RSAUtils.encryptByPublicKey(userFlag,publicKey);
            String qid=parse.getString("id");
            MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
            params.add("qid",qid);
            params.add("userFlag",encFlag);
            String auditKey=SHAutil.getSHA(parse.getString("t"))+"-"+userFlag;
            stringRedisTemplate.opsForList().rightPush(auditKey,qid);
            System.out.println(auditKey);
            System.out.println(qid);
            httpClient.call("/audit/dobatchproof", HttpMethod.POST,params);
            ResponseMsg responseMsg = new ResponseMsg(688, batchAudit.dataID);

            return SerializeUtil.doSerialize(responseMsg);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public String doBatchProof(HttpServletRequest request) throws Exception, InterruptedException {
        System.out.println("batch_proof");
        String qid=request.getParameter("qid");
        System.out.println(qid);
        String encFlag = request.getParameter("userFlag");
        byte[] queryOneAsset = contract.createTransaction("ReadAuditAsset")
                .submit(qid);
        JSONObject parse = (JSONObject) JSON.parse(queryOneAsset);
        String auditor=parse.getString("auditor");
        String auditee=parse.getString("auditee");
        String logType="batch_proof";
        String clustert=parse.getString("t");
        String clusterz = parse.getString("z");
        String clusterr1=parse.getString("r1");
        String clusterr2=parse.getString("r2");
        String[] t=clustert.split(",");
        String[] z = clusterz.split(",");
        String[] r1=clusterr1.split(",");
        String[] r2=clusterr2.split(",");
        int len=t.length;
        List<Future<BatchAuditParam>> list=new ArrayList<>();
        List<BatchAuditParam> proofs=new ArrayList<>();
        for (int i = 0; i < len; i++) {
            Future<BatchAuditParam> future = executorService.submit(new ProofCallable(depMapper, sysParam, TempSys.URL, TempSys.prefix,t[i],
                    Integer.parseInt(z[i]), r1[i],r2[i],i));
            list.add(future);
        }
        for (int i = 0; i < len; i++) {
            Future<BatchAuditParam> future = list.get(i);
            while (!future.isDone()){}
            proofs.add(future.get());
        }
        proofs.sort((m,n)->{
            return m.index-n.index;
        });
        String proof="";
        for (int i = 0; i < proofs.size(); i++) {
            proof+=proofs.get(i).proof;
            if(i!=proofs.size()-1) proof+="<#>";
        }
        System.out.println("proof阶段=======");
        String id= SHAutil.getSHA(UUID.randomUUID().toString()+new Date().getTime());
        byte[] invokeResult = contract.createTransaction("CreateAuditAsset")
                .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                .submit(id,clustert,auditor,auditee,logType,clusterz, clusterr1,clusterr2,proof,"2","","");
        JSONObject res =(JSONObject) JSON.parse(invokeResult);
        String proofid=res.getString("id");

        MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
        params.add("qid",proofid);
        params.add("userFlag",request.getParameter("userFlag"));
        System.out.println(proofid);
        httpClient.call("/audit/dobatchverify", HttpMethod.POST,params);
        return "";
    }
    public String doBatchVerify(HttpServletRequest request) throws Exception, InterruptedException {
        String qid=request.getParameter("qid");
        String encFlag = request.getParameter("userFlag");
        RSAPrivateKey privateKey = RSAUtils.getPrivateKey(TempSys.privateKey);
        String userFlag= RSAUtils.decryptByPrivateKey(encFlag,privateKey);
        byte[] queryOneAsset = contract.createTransaction("ReadAuditAsset")
                .submit(qid);
        JSONObject parse = (JSONObject) JSON.parse(queryOneAsset);
        String auditor=parse.getString("auditor");
        String auditee=parse.getString("auditee");
        String logType="batch_verify";
        String clustert=parse.getString("t");
        String clusterz = parse.getString("z");
        String clusterr1=parse.getString("r1");
        String clusterr2=parse.getString("r2");
        String clusterproof=parse.getString("proof");
        String[] t=clustert.split(",");
        String[] z = clusterz.split(",");
        String[] r1=clusterr1.split(",");
        String[] r2=clusterr2.split(",");
        String[] proof=clusterproof.split("<#>");
        List<Future<BatchAuditParam>> list=new ArrayList<>();
        List<BatchAuditParam> vids=new ArrayList<>();
        int len=t.length;
        for (int i = 0; i < len; i++) {
            Future<BatchAuditParam> future = executorService.submit(new VerifyCallable(i,t[i],Integer.parseInt(z[i])
            ,r1[i],r2[i],proof[i],depMapper,sysParam));
            list.add(future);
        }
        long l = System.currentTimeMillis();
        for (int i = 0; i < len; i++) {
            Future<BatchAuditParam> future = list.get(i);
            while (!future.isDone()){}
            vids.add(future.get());
        }
        long l1 = System.currentTimeMillis();
        vids.sort((m,n)->{
            return m.index-n.index;
        });
        Element sigam=null;
        Element right=null;
        for(BatchAuditParam es:vids){
            sigam=(sigam==null?es.sigma:sigam.duplicate().mul(es.sigma));
            right=(right==null?es.rightPair:right.duplicate().mul(es.rightPair));
        }
        String rightSumStr="";
        for (int i = 0; i < vids.size(); i++) {
            rightSumStr+=ElementUtil.e2str(vids.get(i).rightSum);
            if(i==vids.size()-1) rightSumStr+="<#>";
        }
        Element left=sysParam.pr.pairing(sigam,sysParam.g);
        String result="0";
        if (right.isEqual(left)) result="1";
        String id= SHAutil.getSHA(UUID.randomUUID().toString()+new Date().getTime());
        byte[] invokeResult = contract.createTransaction("CreateAuditAsset")
                .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                .submit(id,clustert,auditor,auditee,logType,clusterz, clusterr1,
                        clusterr2,clusterproof,result,ElementUtil.e2str(sigam),rightSumStr);
        JSONObject res =(JSONObject) JSON.parse(invokeResult);
        String auditKey=SHAutil.getSHA(parse.getString("t"))+"-"+userFlag;
        stringRedisTemplate.opsForList().rightPush(auditKey,qid);
        stringRedisTemplate.opsForList().rightPush(auditKey,res.getString("id"));
        System.out.println(auditKey);
        System.out.println(res.getString("id"));
        System.out.println(result);
        System.out.println(res.getString("id"));
        System.out.println("verify阶段=======");
        System.out.println("批量审计verify时间:"+(l1-l));
        return res.getString("id");
    }
    public byte[] isAuditFin(HttpServletRequest request){
        Message message=null;
        String token = request.getHeader("token");
        String userFlag=TokenUtil.getUflag(token);
        try {
            message= (Message) SerializeUtil.doDeserialize(HttpUtil.getData(request));
            String t=message.t;
            String key=t+"-"+userFlag;
            List<String> range = stringRedisTemplate.opsForList().range(key, 0, -1);
            if(range!=null&&range.size()==3){
                ResponseMsg responseMsg = new ResponseMsg(1024, message.dataID);
                responseMsg.setIds(range);
                stringRedisTemplate.delete(key);
                return SerializeUtil.doSerialize(responseMsg);
            }
            else return SerializeUtil.doSerialize(new ResponseMsg(1023,message.dataID));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
