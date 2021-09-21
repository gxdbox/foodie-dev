package com.imooc.enums;

/**
 * 支付方式枚举
 */
public enum PayEnum {
    WEIXIN(1,"微信支付"),
    ZHIFUBAO(2,"支付宝支付");
    public final Integer type;
    public final String value;

    PayEnum(Integer type, String method) {
        this.type = type;
        value = method;
    }
}
