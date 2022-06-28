package com.csdaa.wsn.client.controller;

import com.csdaa.wsn.client.service.AuditService;
import com.csdaa.wsn.commons.entity.AuditLogs;
import com.csdaa.wsn.commons.entity.FileCallBack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    AuditService auditService;
    @RequestMapping("/doaudit")
    public FileCallBack doAudit(String t,String upid){
        return auditService.doAudit(t,upid);
    }

    @RequestMapping("/getlogs")
    public List<AuditLogs> getLogs(String t){
        return auditService.getLogs(t);
    }

    @RequestMapping("/batchaudit")
    public FileCallBack doBatchAudit(String []batchT,String []batchUpid){
        return auditService.doBatchAudit(batchT,batchUpid);
    }
    @RequestMapping("/isfin")
    public FileCallBack isAuditFin(){
        return auditService.isAuditFin();
    }
}
