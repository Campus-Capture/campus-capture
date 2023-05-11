package com.github.campus_capture.bootcamp.shop;

public class PowerUp {
    private String name;
    private int fund;
    private int value;


    public PowerUp(String name, int fundOfSection, int value){
        this.name = name;
        this.fund = fundOfSection;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getFund() {
        return fund;
    }

    public int getValue() {
        return value;
    }
}
