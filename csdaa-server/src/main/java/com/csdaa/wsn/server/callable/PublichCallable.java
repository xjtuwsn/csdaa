package com.csdaa.wsn.server.callable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csdaa.wsn.commons.entity.BatchAuditParam;
import com.csdaa.wsn.commons.entity.ResponseMsg;
import com.csdaa.wsn.commons.entity.SysParam;
import com.csdaa.wsn.server.mapper.DepMapper;
import com.csdaa.wsn.server.utils.ElementUtil;
import com.csdaa.wsn.server.utils.FabricFactory;
import com.csdaa.wsn.server.utils.SHAutil;
import com.csdaa.wsn.server.utils.SerializeUtil;
import it.unisa.dia.gas.jpbc.Element;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.sdk.Peer;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * @project:csdaa-server
 * @file:PublichCallable
 * @author:wsn
 * @create:2022/4/27-15:03
 */
public class PublichCallable implements Callable<BatchAuditParam> {
    public String t;
    public String upid;
    public String auditor;
    public String auditee;
    public SysParam sysParam;
    public DepMapper depMapper;
    public Contract contract;
    public String token;
    public int index;
    public PublichCallable(String t, String upid, String auditor, String auditee, SysParam sysParam,
                           DepMapper depMapper, Contract contract,String token,int index) {
        this.t = t;
        this.token=token;
        this.upid = upid;
        this.auditor = auditor;
        this.auditee = auditee;
        this.sysParam = sysParam;
        this.depMapper = depMapper;
        this.contract = contract;
        this.index=index;
    }

    @Override
    public BatchAuditParam call() throws Exception {
        byte[] queryOneAsset = contract.createTransaction("ReadFileAsset")
                .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                .submit(upid);
        JSONObject fileindex = (JSONObject) JSON.parse(queryOneAsset);
        if(!fileindex.getString("t").equals(t)||!fileindex.getString("user").equals(token))
            return null;
        Integer cnt = depMapper.isFileExist(t);
        Random random=new Random();
        int z=random.nextInt(cnt)+1;
        Element r1=sysParam.zr.newRandomElement();
        Element r2=sysParam.zr.newRandomElement();
//        String id= SHAutil.getSHA(t+new Date().getTime());
        //String logType="challenge";
//        byte[] invokeResult = contract.createTransaction("CreateAuditAsset")
//                .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
//                .submit(id,t,auditor,auditee,logType,z+"", ElementUtil.e2str(r1),ElementUtil.e2str(r2),"","2","");
//        JSONObject parse =(JSONObject) JSON.parse(invokeResult);
//        String qid=parse.getString("id");
        return new BatchAuditParam(index,z+"",ElementUtil.e2str(r1),ElementUtil.e2str(r2));
    }
}
