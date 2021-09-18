package com.imooc.pojo.vo;

import lombok.Data;

@Data
public class CommentLevelCountVO {
    private Integer goodCommentCount;
    private Integer mediumCommentCount;
    private Integer badCommentCount;
    private Integer totalCommentCount;
}
