package com.csdaa.wsn.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class LocalFile {
    public String t;
    public String sk;
    public String filename;
    public String uptime;
    public String upid;
    public String localName;
}
