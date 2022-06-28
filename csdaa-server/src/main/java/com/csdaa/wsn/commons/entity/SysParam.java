package com.csdaa.wsn.commons.entity;

import com.csdaa.wsn.server.config.TempSys;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;

import java.util.Base64;

public class SysParam {
    private static int rbits=160;
    private static int qbits=512;
    private TypeACurveGenerator pg;
    private PairingParameters gg ;
    public Pairing pr;
    public Field g1 ;
    private Field g2 ;
    public Field zr ;
    public Element g;
    public Element u;
    public Element sk;
    public Element t ;
    public Element hash1;
    public Element hash2;
    public SysParam(){
        pg=new TypeACurveGenerator(rbits,qbits);
        gg = pg.generate();
        pr= PairingFactory.getPairing("/usr/soft/config/jpbc.properties");
//        pr= PairingFactory.getPairing("E:\\csdaa-server\\src\\main\\resources\\jpbc.properties");
        g1 = pr.getG1();
        g2 = pr.getG2();
        zr = pr.getZr();
        g=g1.newElementFromBytes(Base64.getDecoder().decode(TempSys.g));
        u=g1.newElementFromBytes(Base64.getDecoder().decode(TempSys.u));
        hash1 = zr.newElementFromBytes(Base64.getDecoder().decode(TempSys.hash1));
        hash2 = g1.newElementFromBytes(Base64.getDecoder().decode(TempSys.hash2));
    }
}