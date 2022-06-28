package com.csdaa.wsn.client.controller;

import java.util.concurrent.Callable;

/**
 * @project:JavaCode
 * @file:MyRunnable
 * @author:wsn
 * @create:2022/4/27-13:35
 */
public class MyRunnable implements Callable<Integer> {

    public int start;
    public int end;
    MyRunnable(int start, int end){
        this.start=start;
        this.end=end;
    }
    @Override
    public Integer call() throws Exception {
        int sum=0;
        //Thread.sleep(100);
        for (int i = start; i <end ; i++) {
            if(isPrime(i))sum++;
        }
        return sum;
    }
    public boolean isPrime(int num) {
        for (int i=2; i<=num/2; i++) {
            if(num % i == 0) return false;
            }
        return true;
    }
}
