package com.csdaa.wsn.commons.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @project:csdaa-server
 * @file:BatchAudit
 * @author:wsn
 * @create:2022/4/27-15:28
 */
@Data
@Getter
@Setter
public class BatchAudit implements Serializable {
    public int code;
    public String dataID;
    public List<String> ts;
    public List<String> upids;

    public BatchAudit(int code, String dataID, List<String> ts, List<String> upids) {
        this.code = code;
        this.dataID = dataID;
        this.ts = ts;
        this.upids = upids;
    }
}
