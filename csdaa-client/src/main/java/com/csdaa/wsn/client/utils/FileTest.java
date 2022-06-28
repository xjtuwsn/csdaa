package com.csdaa.wsn.client.utils;

import java.io.File;
import java.util.Random;

public class FileTest {
    public static void main(String[] args) {
        Random r=new Random();
        r.setSeed(15465465);
        for (int i = 0; i < 5; i++) {
            System.out.println(r.nextInt(500));
        }
    }
}
