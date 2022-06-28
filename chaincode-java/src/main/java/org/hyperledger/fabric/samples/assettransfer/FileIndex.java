package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

/**
 * @project:basic
 * @file:FileIndex
 * @author:wsn
 * @create:2022/4/14-12:36
 */
@DataType()
public final class FileIndex {
    @Property
    public final String t;
    @Property
    public final String user;
    @Property
    public final String ssp;
    public FileIndex(@JsonProperty("t") final String t, @JsonProperty("user") final String user, @JsonProperty("ssp") final String ssp) {
        this.t = t;
        this.user = user;
        this.ssp = ssp;
    }

    public String getT() {
        return t;
    }

    public String getUser() {
        return user;
    }

    public String getSsp() {
        return ssp;
    }
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        FileIndex other = (FileIndex) obj;

        return Objects.deepEquals(
                new String[] {getT(), getUser(), getSsp()},
                new String[] {other.getT(), other.getUser(), other.getSsp()});
    }
    @Override
    public int hashCode() {
        return Objects.hash(getT(), getUser(), getSsp());
    }
    @Override
    public String toString() {

        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + "{"
                + "t='" + t + '\''
                + ", user='" + user + '\''
                +  ", ssp='" + ssp + '\''
                + '}';
    }
}
