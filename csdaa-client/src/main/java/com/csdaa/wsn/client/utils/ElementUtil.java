package com.csdaa.wsn.client.utils;


import it.unisa.dia.gas.jpbc.Element;

import java.util.Base64;

public class ElementUtil {
    public static String e2str(Element e){
        byte[] bytes = e.toBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
