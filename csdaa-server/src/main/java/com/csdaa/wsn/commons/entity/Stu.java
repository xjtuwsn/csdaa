package com.csdaa.wsn.commons.entity;

import java.io.Serializable;

public class Stu implements Serializable {
    @Override
    public String toString() {
        return "Stu{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                '}';
    }

    public String name;
    private int age;
    private int sex;

    public Stu(String name, int age, int sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }
}
