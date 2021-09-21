package com.imooc.pojo.vo;

import lombok.Data;

@Data
public class SearchItemVO {
    private String itemId;
    private String itemName;
    private int sellCounts;
    private String imgUrl;
    private int price;
}
