package com.csdaa.wsn.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class Message implements Serializable {
    /**
     * @Param code
     * 200 查询t是否存在
     * 205 不存在时第一次发送t,T,kesi,ck
     * 207 发送完成
     * 210 发送不重复的c
     * 220 发送证明参数
     * 300 审计请求
     * 506
     * 606
     */
    public int code;
    public String dataID;
    public String t;
    public String T;
    public String c;
    public String ck;
    public String kesi;
    public String miu;
    public int index;
    public String filename;
    public int z;
    public String r1;
    public String r2;
    public String upid;
    public Message(int code){
        this.code=code;
    }

    public Message(int code, String dataID, String t, int index) {
        this.code = code;
        this.dataID = dataID;
        this.t = t;
        this.index = index;
    }

    public Message(int code, String dataID) {
        this.code = code;
        this.dataID = dataID;
    }
    public Message(int code, String dataID, String t) {
        this.code = code;
        this.dataID = dataID;
        this.t = t;
    }

    public Message(int code, String dataID, String t, String filename) {
        this.code = code;
        this.dataID = dataID;
        this.t = t;
        this.filename = filename;
    }

    public Message(int code, String dataID, String t, String miu, int z, String r1, String r2) {
        this.code = code;
        this.dataID = dataID;
        this.t = t;
        this.miu = miu;
        this.z = z;
        this.r1 = r1;
        this.r2 = r2;
    }

    public Message(int code, String dataID, String t, String t1, String c, String ck, String kesi, String miu, int index, String filename) {
        this.code = code;
        this.dataID = dataID;
        this.t = t;
        T = t1;
        this.c = c;
        this.ck = ck;
        this.kesi = kesi;
        this.miu = miu;
        this.index = index;
        this.filename=filename;
    }
}
