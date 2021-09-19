package com.imooc.service;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountVO;

import java.util.List;

public interface ItemService {
    Items queryItemById(String id);
    List<ItemsImg> queryItemImgById(String id);
    List<ItemsSpec> queryItemSpecById(String id);
    ItemsParam queryItemsParamById(String id);
    CommentLevelCountVO queryCommentLevelCount(String itemId);

}
