package com.imooc.service.impl;

import com.imooc.enums.CommentEnum;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.CommentLevelCountVO;
import com.imooc.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private ItemsImgMapper itemsImgMapper;
    @Autowired
    private ItemsSpecMapper itemsSpecMapper;
    @Autowired
    private ItemsParamMapper itemsParamMapper;
    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemById(String id) {
        Items items = itemsMapper.selectByPrimaryKey(id);
        return items;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgById(String id) {
        Example example = new Example(ItemsImg.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemId",id);
        List<ItemsImg> itemsImgs = itemsImgMapper.selectByExample(example);
        return itemsImgs;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecById(String id) {
        Example example = new Example(ItemsSpec.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemId",id);
        List<ItemsSpec> itemsSpecs = itemsSpecMapper.selectByExample(example);
        return itemsSpecs;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemsParamById(String id) {
        Example example = new Example(ItemsParam.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemId",id);
        ItemsParam itemsParam = itemsParamMapper.selectOneByExample(example);
        return itemsParam;
    }

    @Override
    public CommentLevelCountVO queryCommentLevelCount(String itemId,Integer level) {
        CommentLevelCountVO commentLevelCountVO = new CommentLevelCountVO();
        if (level == null){
            int goodCommentsCount = getItemCommentsCount(itemId, CommentEnum.GOOD.type);
            int mediumCommentsCount = getItemCommentsCount(itemId, CommentEnum.MEDIUM.type);
            int badCommentsCount = getItemCommentsCount(itemId, CommentEnum.BAD.type);
            int totalCommentCount = goodCommentsCount + mediumCommentsCount + badCommentsCount;

            commentLevelCountVO.setBadCommentCount(badCommentsCount);
            commentLevelCountVO.setMediumCommentCount(mediumCommentsCount);
            commentLevelCountVO.setGoodCommentCount(goodCommentsCount);
            commentLevelCountVO.setTotalCommentCount(totalCommentCount);
        }else {
            int itemCommentsCount = getItemCommentsCount(itemId, level);
            switch (level){
                case 1: commentLevelCountVO.setGoodCommentCount(itemCommentsCount);
                break;
                case 2:commentLevelCountVO.setMediumCommentCount(itemCommentsCount);
                break;
                case 3:commentLevelCountVO.setBadCommentCount(itemCommentsCount);
            }
        }





        return commentLevelCountVO;
    }


    private int getItemCommentsCount(String itemId, Integer level) {
        ItemsComments itemsComments = new ItemsComments();
        itemsComments.setItemId(itemId);
        if (level != null){
            itemsComments.setCommentLevel(level);
        }
        return itemsCommentsMapper.selectCount(itemsComments);
    }


}
