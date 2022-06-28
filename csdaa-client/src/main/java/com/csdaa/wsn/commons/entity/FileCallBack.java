package com.csdaa.wsn.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class FileCallBack {
    public int code;
    public String msg;
    public int recordNum;
    public int totalNum;
}
