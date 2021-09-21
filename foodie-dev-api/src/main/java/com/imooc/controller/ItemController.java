package com.imooc.controller;

import com.imooc.enums.YesOrNoEnum;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.CommentLevelCountVO;
import com.imooc.pojo.vo.ItemInfoVO;
import com.imooc.pojo.vo.ShopcartVO;
import com.imooc.service.ItemService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
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
public class ItemController extends BaseController{
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
    public IMOOCJSONResult commentsLevel(@RequestParam String itemId){
        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        CommentLevelCountVO commentLevelCountVO = itemService.queryCommentLevelCount(itemId);
        return IMOOCJSONResult.ok(commentLevelCountVO);
    }

    @GetMapping("/comments")
    @ApiOperation(value = "查询商品评论" , notes = "查询商品评论", httpMethod = "GET")
    public IMOOCJSONResult queryItemComment(
            @ApiParam(name = "itemId",value = "商品id", required = true)
                                    @RequestParam String itemId,
            @ApiParam(name = "level", value = "评价等级", required = false)
                                    @RequestParam Integer level,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
                                    @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
                                    @RequestParam Integer pageSize){
        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid = itemService.queryItemComment(itemId,level,page,pageSize);
        return IMOOCJSONResult.ok(grid);
    }

    @GetMapping("/search")
    @ApiOperation(value = "搜索商品列表" , notes = "搜索商品列表", httpMethod = "GET")
    public IMOOCJSONResult search(
            @ApiParam(name = "keywords",value = "关键字", required = false)
            @RequestParam String keywords,
            @ApiParam(name = "sort", value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize){
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedGridResult grid = itemService.search(keywords,sort,page,pageSize);
        return IMOOCJSONResult.ok(grid);
    }

    @GetMapping("/catItems")
    @ApiOperation(value = "根据catid搜索商品列表" , notes = "根据catid搜索商品列表", httpMethod = "GET")
    public IMOOCJSONResult search(
            @ApiParam(name = "catId",value = "三级分类id", required = true)
            @RequestParam Integer catId,
            @ApiParam(name = "sort", value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize){
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedGridResult grid = itemService.searchByCat(catId,sort,page,pageSize);
        return IMOOCJSONResult.ok(grid);
    }

    @GetMapping("/refresh")
    @ApiOperation(value = "根据规格id刷新购物车商品信息" , notes = "根据规格id刷新购物车商品信息", httpMethod = "GET")
    public IMOOCJSONResult refresh(
            @ApiParam(name = "itemSpecIds",value = "规格ids", required = true)
            @RequestParam String itemSpecIds){
        if (StringUtils.isBlank(itemSpecIds)){
            return IMOOCJSONResult.errorMsg("规格id不能为空");
        }
        List<ShopcartVO> shopcartVOS = itemService.queryItemBySpecId(itemSpecIds);
        return IMOOCJSONResult.ok(shopcartVOS);
    }



}
