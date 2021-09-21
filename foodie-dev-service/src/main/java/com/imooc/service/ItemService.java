package com.imooc.service;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountVO;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.ShopcartVO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface ItemService {
    Items queryItemById(String id);
    List<ItemsImg> queryItemImgById(String id);
    String queryItemImgByImgId(String itemId);
    List<ItemsSpec> queryItemSpecById(String id);
    ItemsSpec queryItemSpecBySpecId(String specId);

    ItemsParam queryItemsParamById(String id);
    CommentLevelCountVO queryCommentLevelCount(String itemId);
    PagedGridResult queryItemComment(String itemId, Integer level, Integer page, Integer size);
    PagedGridResult search(String itemName, String sort, Integer page, Integer size);
    PagedGridResult searchByCat(Integer catId, String sort, Integer page, Integer size);
    /**
     * 根据规格ids查询最新的购物车中商品数据（用于刷新渲染购物车中的商品数据）
     * @param specIds
     * @return
     */
    public List<ShopcartVO> queryItemBySpecId(String specIds);

    void decreaseItemStock(String specId, int buyCounts);
}
