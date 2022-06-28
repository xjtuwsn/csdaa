package com.csdaa.wsn.server.callable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csdaa.wsn.commons.entity.BatchAuditParam;
import com.csdaa.wsn.commons.entity.SysParam;
import com.csdaa.wsn.server.mapper.DepMapper;
import com.csdaa.wsn.server.utils.ElementUtil;
import com.csdaa.wsn.server.utils.FabricFactory;
import com.csdaa.wsn.server.utils.SHAutil;
import it.unisa.dia.gas.jpbc.Element;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.sdk.Peer;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * @project:csdaa-server
 * @file:VerifyCallable
 * @author:wsn
 * @create:2022/4/27-15:18
 */
public class VerifyCallable implements Callable<BatchAuditParam> {
    public int index;
    public String t;
    public int z;
    public String strr1;
    public String strr2;
    public String strproof;
    public DepMapper depMapper;
    public SysParam sysParam;

    public VerifyCallable(int index, String t, int z, String strr1, String strr2, String strproof, DepMapper depMapper, SysParam sysParam) {
        this.index = index;
        this.t = t;
        this.z = z;
        this.strr1 = strr1;
        this.strr2 = strr2;
        this.strproof = strproof;
        this.depMapper = depMapper;
        this.sysParam = sysParam;
    }

    @Override
    public BatchAuditParam call() throws Exception {

        Element r1 = sysParam.zr.newElementFromBytes(Base64.getDecoder().decode(strr1));
        Element r2 = sysParam.zr.newElementFromBytes(Base64.getDecoder().decode(strr2));
        Element proof=sysParam.zr.newElementFromBytes(Base64.getDecoder().decode(strproof));
        Random random=new Random();
        Element leftSum=null;
        Element rightSum=null;
        for(int i=1;i<=z;i++){
            random.setSeed(r1.toBigInteger().longValue());
            int ai=random.nextInt(z)+1;
            random.setSeed(r2.toBigInteger().longValue());
            Element bi=sysParam.zr.newElement(random.nextInt(z)+1);
            String kesiStr = depMapper.getKesi(t, ai-1);
            Element kesi=sysParam.g1.newElementFromBytes(Base64.getDecoder().decode(kesiStr));
            String Tstr=depMapper.getTag(t,ai-1);
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
        //Element left = sysParam.pr.pairing(leftSum, sysParam.g);
        Element right = sysParam.pr.pairing(rightSum.duplicate().mul(sysParam.u.duplicate().powZn(proof)),
                sysParam.g1.newElementFromBytes(Base64.getDecoder().decode(t)));
//        String result="0";
//        if(left.isEqual(right)) result="1";
//        byte[] invokeResult = contract.createTransaction("CreateAuditAsset")
//                .setEndorsingPeers(FabricFactory.network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
//                .submit(id,parse.getString("t"),auditor,auditee,logType,z+"", ElementUtil.e2str(r1),
//                        ElementUtil.e2str(r2),ElementUtil.e2str(proof),result,ElementUtil.e2str(leftSum));
//        JSONObject res =(JSONObject) JSON.parse(invokeResult);
//        return res.getString("id");

        return new BatchAuditParam(index,leftSum,rightSum,right);
    }
}
