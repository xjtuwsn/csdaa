package com.csdaa.wsn.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class AuditLogs {
    public String id;
    public String date;
    public String t;
    public String auditor;
    public String auditee;
    public String z;
    public String r1;
    public String r2;
    public String proof;
    public int result;
    public String sigma;
}
