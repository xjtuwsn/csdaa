package com.csdaa.wsn.commons.entity;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

/**
 * @project:csdaa-server
 * @file:PublishParam
 * @author:wsn
 * @create:2022/5/28-13:05
 */
@Data
public class BatchAuditParam {
    public int index;
    public String z;
    public String r1;
    public String r2;
    public String proof;
    public Element sigma;
    public Element rightSum;
    public Element rightPair;
    public BatchAuditParam(int index, String z, String r1, String r2) {
        this.index = index;
        this.z = z;
        this.r1 = r1;
        this.r2 = r2;
    }

    public BatchAuditParam(int index, String proof) {
        this.index = index;
        this.proof = proof;
    }

    public BatchAuditParam(int index, Element sigma,Element rightSum, Element rightPair) {
        this.index = index;
        this.sigma = sigma;
        this.rightSum=rightSum;
        this.rightPair = rightPair;
    }
}
