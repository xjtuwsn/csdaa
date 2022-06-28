package com.csdaa.wsn.commons.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Getter
@Setter
public class ResponseMsg implements Serializable {
    /**
     * 400 重复
     * 300 不重复
     */
    public int code;
    public String dataId;
    public int recordNum;
    public int z;
    public String r1;
    public String r2;
    public String c;
    public String ck;
    public List<String> ids;
    public String upid;
    public String token;
    public ResponseMsg(String dataId) {
        this.dataId = dataId;
    }

    public ResponseMsg(int code, String dataId) {
        this.code = code;
        this.dataId = dataId;
    }

    public ResponseMsg(int code, String dataId, String token) {
        this.code = code;
        this.dataId = dataId;
        this.token = token;
    }

    public ResponseMsg(int code, String dataId, String c, String ck) {
        this.code = code;
        this.dataId = dataId;
        this.c = c;
        this.ck = ck;
    }

    public ResponseMsg(int code, String dataId, int recordNum,String upid) {
        this.code = code;
        this.dataId = dataId;
        this.recordNum = recordNum;
        this.upid=upid;
    }

    public ResponseMsg(int code, String dataId, int z, String r1, String r2) {
        this.code = code;
        this.dataId = dataId;
        this.z = z;
        this.r1 = r1;
        this.r2 = r2;
    }

}
