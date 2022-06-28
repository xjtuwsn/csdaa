package com.csdaa.wsn.server.utils;

import java.io.IOException;

/**
 * @project:csdaa-server
 * @file:ExecShellUtil
 * @author:wsn
 * @create:2022/6/10-22:20
 */
public class ExecShellUtil {

    public static int exec(String cmd){
        String prefix1 = "/bin/sh";
        String prefix2 = "-c";
        try {
            Process p = Runtime.getRuntime().exec(new String[]{prefix1, prefix2, cmd});
            int returnCode = p.waitFor();
            return returnCode;
        } catch (IOException |InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
