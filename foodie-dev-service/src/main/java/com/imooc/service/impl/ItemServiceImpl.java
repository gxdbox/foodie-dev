package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.CommentEnum;
import com.imooc.enums.YesOrNoEnum;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.CommentLevelCountVO;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.SearchItemVO;
import com.imooc.pojo.vo.ShopcartVO;
import com.imooc.service.ItemService;
import com.imooc.utils.DesensitizationUtil;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

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
    @Autowired
    private ItemsMapperCustom itemsMapperCustom;

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
    public String queryItemImgByImgId(String itemId) {
        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNoEnum.YES.type);
        ItemsImg result = itemsImgMapper.selectOne(itemsImg);
        return result != null ? result.getUrl():"";
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
    public ItemsSpec queryItemSpecBySpecId(String specId) {
        return itemsSpecMapper.selectByPrimaryKey(specId);
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
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentLevelCountVO queryCommentLevelCount(String itemId) {
        CommentLevelCountVO commentLevelCountVO = new CommentLevelCountVO();
        int goodCommentsCount = getItemCommentsCount(itemId, CommentEnum.GOOD.type);
        int mediumCommentsCount = getItemCommentsCount(itemId, CommentEnum.MEDIUM.type);
        int badCommentsCount = getItemCommentsCount(itemId, CommentEnum.BAD.type);
        int totalCommentCount = goodCommentsCount + mediumCommentsCount + badCommentsCount;
        commentLevelCountVO.setBadCounts(badCommentsCount);
        commentLevelCountVO.setNormalCounts(mediumCommentsCount);
        commentLevelCountVO.setGoodCounts(goodCommentsCount);
        commentLevelCountVO.setTotalCounts(totalCommentCount);
        return commentLevelCountVO;
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryItemComment(String itemId, Integer level, Integer page, Integer pageSize) {
        PagedGridResult result = new PagedGridResult();
        /**
         * page: 第几页
         * pageSize: 每页显示条数
         */
        PageHelper.startPage(page, pageSize);

        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("level", level);
        List<ItemCommentVO> list = itemsMapperCustom.queryItemComment(map);
        for (ItemCommentVO vo : list) {
            vo.setNickname(DesensitizationUtil.commonDisplay(vo.getNickname()));
        }
        return setterGrid(page, list);
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult search(String itemName, String sort, Integer page, Integer size) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("keywords",itemName);
        map.put("sort",sort);
        PageHelper.startPage(page,size);
        List<SearchItemVO> list = itemsMapperCustom.search(map);

        return setterGrid(page,list);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchByCat(Integer catId, String sort, Integer page, Integer size) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("catId",catId);
        map.put("sort",sort);
        PageHelper.startPage(page,size);
        List<SearchItemVO> list = itemsMapperCustom.searchByCat(map);

        return setterGrid(page,list);
    }

    @Override
    public List<ShopcartVO> queryItemBySpecId(String specIds) {
        String ids[] = specIds.split(",");
        List<String> specIdList = new ArrayList<>();
        Collections.addAll(specIdList,ids);
        return itemsMapperCustom.queryItemsBySpecIds(specIdList);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void decreaseItemStock(String specId, int buyCounts) {
        int i = itemsMapperCustom.decreaseItemStock(specId, buyCounts);
        if (i != 1){
            throw new RuntimeException("创建订单失败");
        }
    }

    private PagedGridResult setterGrid(Integer page, List<?> list) {
        PagedGridResult result = new PagedGridResult();
        PageInfo<?> pageInfo = new PageInfo<>(list);
        result.setTotal(page);
        result.setRows(list);
        result.setPage(pageInfo.getPages());
        result.setRecords(pageInfo.getTotal());
        return result;
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
