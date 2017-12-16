package com.example.sqlitedemo;

/**
 * Description:
 * Author: qiubing
 * Date: 2017-12-16 19:21
 */
public class Person {
    public int _id;
    public String name;
    public int age;
    public String info;
    public String other;

    public Person(){}

    public Person(String name,int age,String info){
        this.name = name;
        this.age = age;
        this.info = info;
    }
}
