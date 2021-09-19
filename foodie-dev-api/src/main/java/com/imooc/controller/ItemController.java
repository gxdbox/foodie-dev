package com.imooc.controller;

import com.imooc.enums.YesOrNoEnum;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.CommentLevelCountVO;
import com.imooc.pojo.vo.ItemInfoVO;
import com.imooc.service.ItemService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("items")
@Api(value = "商品详情",tags = "商品详情相关接口")
public class ItemController {
    @Autowired
    private ItemService  itemService;

    @GetMapping("/info/{itemId}")
    @ApiOperation(value = "获取商品详情页" , notes = "获取商品详情页", httpMethod = "GET")
    public IMOOCJSONResult detail(@ApiParam(name = "itemId",value = "商品id",required = true)
                                      @PathVariable String itemId){
        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }

        Items items = itemService.queryItemById(itemId);
        List<ItemsImg> itemsImgs = itemService.queryItemImgById(itemId);
        List<ItemsSpec> itemsSpecs = itemService.queryItemSpecById(itemId);
        ItemsParam itemsParam = itemService.queryItemsParamById(itemId);
        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(items);
        itemInfoVO.setItemParams(itemsParam);
        itemInfoVO.setItemImgList(itemsImgs);
        itemInfoVO.setItemSpecList(itemsSpecs);
        return IMOOCJSONResult.ok(itemInfoVO);
    }

    @GetMapping("/commentLevel")
    @ApiOperation(value = "获取商品评价" , notes = "获取商品评价", httpMethod = "GET")
    public IMOOCJSONResult comments(@RequestParam String itemId){
        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        CommentLevelCountVO commentLevelCountVO = itemService.queryCommentLevelCount(itemId);
        return IMOOCJSONResult.ok(commentLevelCountVO);
    }
}
