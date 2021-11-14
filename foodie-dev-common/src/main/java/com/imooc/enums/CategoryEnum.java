package com.imooc.enums;

public enum CategoryEnum {
    One(1, "一级分类"),
    Two(2, "二级分类"),
    Three(3, "三级分类");
    public final Integer type;
    private final String value;

    CategoryEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
