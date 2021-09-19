package com.imooc.pojo.vo;

import lombok.Data;

@Data
public class CommentLevelCountVO {
    private Integer goodCounts;
    private Integer normalCounts;
    private Integer badCounts;
    private Integer totalCounts;
}
