package com.imooc.enums;

public enum CommentEnum {
    GOOD(1,"好评"),
    MEDIUM(2,"中评"),
    BAD(3,"差评");

    public final Integer type;
    private final String value;

    CommentEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
