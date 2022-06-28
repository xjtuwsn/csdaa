package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

/**
 * @project:basic
 * @file:SystemParamter
 * @author:wsn
 * @create:2022/4/15-16:46
 */
@DataType()
public final class SystemParamter {
    @Property
    public final String type;
    @Property
    public final String q;
    @Property
    public final String r;
    @Property
    public final String h;
    @Property
    public final String exp1;
    @Property
    public final String exp2;
    @Property
    public final String sign0;
    @Property
    public final String sign1;
    @Property
    public final String g;
    @Property
    public final String u;
    @Property
    public final String hash1;
    @Property
    public final String hash2;

    public SystemParamter(@JsonProperty("type") final String type, @JsonProperty("q") final String q,
                          @JsonProperty("r") final String r,
                          @JsonProperty("h") final String h, @JsonProperty("exp1") final String exp1,
                          @JsonProperty("exp2") final String exp2,
                          @JsonProperty("sign0") final String sign0, @JsonProperty("sign1") final String sign1,
                          @JsonProperty("g") final String g,
                          @JsonProperty("u") final String u, @JsonProperty("hash1") final String hash1,
                          @JsonProperty("hash2") final String hash2) {
        this.type = type;
        this.q = q;
        this.r = r;
        this.h = h;
        this.exp1 = exp1;
        this.exp2 = exp2;
        this.sign0 = sign0;
        this.sign1 = sign1;
        this.g = g;
        this.u = u;
        this.hash1 = hash1;
        this.hash2 = hash2;
    }

    public String getType() {
        return type;
    }

    public String getQ() {
        return q;
    }

    public String getR() {
        return r;
    }

    public String getH() {
        return h;
    }

    public String getExp1() {
        return exp1;
    }

    public String getExp2() {
        return exp2;
    }

    public String getSign0() {
        return sign0;
    }

    public String getSign1() {
        return sign1;
    }

    public String getG() {
        return g;
    }

    public String getU() {
        return u;
    }

    public String getHash1() {
        return hash1;
    }

    public String getHash2() {
        return hash2;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemParamter)) {
            return false;
        }
        SystemParamter that = (SystemParamter) o;
        return Objects.equals(getType(), that.getType()) && Objects.equals(getQ(), that.getQ()) && Objects.equals(getR(), that.getR()) && Objects.equals(getH(), that.getH()) && Objects.equals(getExp1(), that.getExp1()) && Objects.equals(getExp2(), that.getExp2()) && Objects.equals(getSign0(), that.getSign0()) && Objects.equals(getSign1(), that.getSign1()) && Objects.equals(getG(), that.getG()) && Objects.equals(getU(), that.getU()) && Objects.equals(getHash1(), that.getHash1()) && Objects.equals(getHash2(), that.getHash2());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getQ(), getR(), getH(), getExp1(), getExp2(), getSign0(), getSign1(), getG(), getU(), getHash1(), getHash2());
    }

    @Override
    public String toString() {
        return "SystemParamter{"
                + "type='" + type + '\''
                + ", q='" + q + '\''
                + ", r='" + r + '\''
                + ", h='" + h + '\''
                + ", exp1='" + exp1 + '\''
                + ", exp2='" + exp2 + '\''
                + ", sign0='" + sign0 + '\''
                + ", sign1='" + sign1 + '\''
                + ", g='" + g + '\''
                + ", u='" + u + '\''
                + ", hash1='" + hash1 + '\''
                + ", hash2='" + hash2 + '\''
                + '}';
    }
}
