/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class AuditAssert {
    @Property
    public final String id;
    @Property
    public final String t;
    @Property
    public final String auditor;
    @Property
    public final String auditee;
    @Property
    public final String logType;
    @Property
    public final String z;
    @Property
    public final String r1;
    @Property
    public final String r2;
    @Property
    public final String proof;
    @Property
    public final int result;
    @Property
    public final String sigma;

    public AuditAssert(@JsonProperty("id") final String id, @JsonProperty("t") final String t, @JsonProperty("auditor") final String auditor,
                       @JsonProperty("auditee") final String auditee, @JsonProperty("logType") final String logType, @JsonProperty("z") final String  z,
                       @JsonProperty("r1") final String r1, @JsonProperty("r2") final String r2, @JsonProperty("proof") final String proof,
                       @JsonProperty("result") final int result, @JsonProperty("sigma") final String sigma) {
        this.id = id;
        this.t = t;
        this.auditor = auditor;
        this.auditee = auditee;
        this.logType = logType;
        this.z = z;
        this.r1 = r1;
        this.r2 = r2;
        this.proof = proof;
        this.result = result;
        this.sigma = sigma;
    }

    public String getId() {
        return id;
    }

    public String getT() {
        return t;
    }

    public String getAuditor() {
        return auditor;
    }

    public String getAuditee() {
        return auditee;
    }

    public String getLogType() {
        return logType;
    }

    public String getZ() {
        return z;
    }

    public String getR1() {
        return r1;
    }

    public String getR2() {
        return r2;
    }

    public String getProof() {
        return proof;
    }

    public int getResult() {
        return result;
    }

    public String getSigma() {
        return sigma;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        AuditAssert other = (AuditAssert) obj;

        return Objects.deepEquals(
                new String[] {getId(), getT(), getAuditor(), getAuditee(), getLogType(), getZ(), getR1(), getR2(), getProof(), getSigma()},
                new String[] {other.getId(), other.getT(), other.getAuditor(), other.getAuditee(), other.getLogType(), other.getZ(),
                        other.getR1(), other.getR2(), other.getProof(), other.getSigma()})
                &&
                Objects.deepEquals(
                new int[] {getResult()},
                new int[] {other.getResult()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getT(), getAuditor(), getAuditee(), getLogType(), getZ(), getR1(), getR2(), getProof(), getResult(), getSigma());
    }

    @Override
    public String toString() {

        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + "{"
                + "id='" + id + '\''
                + ", t='" + t + '\''
                + ", auditor='" + auditor + '\''
                +  ", auditee='" + auditee + '\''
                + ", logType='" + logType + '\''
                + ", z=" + z
                + ", r1='" + r1 + '\''
                + ", r2='" + r2 + '\''
                + ", proof='" + proof + '\''
                + ", result=" + result
                + ", sigma='" + sigma + '\''
                + '}';
    }
}
