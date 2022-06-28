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

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Base64;
import java.util.Date;
import java.util.EnumSet;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * @project:csdaa-server
 * @file:ProofCallable
 * @author:wsn
 * @create:2022/4/27-15:11
 */
public class ProofCallable implements Callable<BatchAuditParam> {
    public DepMapper depMapper;
    public SysParam sysParam;
    public String URL;
    public String prefix;
    public String t;
    public int z;
    public String strr1;
    public String strr2;
    public int index;
    public ProofCallable(DepMapper depMapper, SysParam sysParam, String URL, String prefix,String t, int z, String strr1, String strr2,int index) {
        this.depMapper = depMapper;
        this.sysParam = sysParam;
        this.URL = URL;
        this.prefix = prefix;
        this.t=t;
        this.z = z;
        this.strr1 = strr1;
        this.strr2 = strr2;
        this.index=index;
    }

    @Override
    public BatchAuditParam call() throws Exception {

        Element r1 = sysParam.zr.newElementFromBytes(Base64.getDecoder().decode(strr1));
        Element r2 = sysParam.zr.newElementFromBytes(Base64.getDecoder().decode(strr2));
        Element proof=null;
        Random random=new Random();
        for(int i=1;i<=z;i++){
            random.setSeed(r1.toBigInteger().longValue());
            int ai=random.nextInt(z)+1;
            random.setSeed(r2.toBigInteger().longValue());
            Element bi=sysParam.zr.newElement(random.nextInt(z)+1);
            String tag = depMapper.getTag(t, ai-1);
            File f=new File(URL+ SHAutil.getSHA(tag)+prefix);
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


        return new BatchAuditParam(index,ElementUtil.e2str(proof));
    }
}
