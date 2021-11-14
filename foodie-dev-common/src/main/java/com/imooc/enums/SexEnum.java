package com.imooc.enums;

public enum SexEnum {
    man(0, "男"),
    woman(1, "女"),
    secret(2, "保密");
    public final Integer type;
    private final String value;

    SexEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
