package com.csdaa.wsn.commons.entity;

import lombok.Data;

@Data
public class AuditAsset {
    public String id;
    public String t;
    public String auditor;
    public String auditee;
    public String logType;
    public int z;
    public String r1;
    public String r2;
    public String proof;
    public int result;
    public String sigma;
}
