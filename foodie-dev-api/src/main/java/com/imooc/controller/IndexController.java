package com.imooc.controller;

import com.imooc.enums.CategoryEnum;
import com.imooc.enums.YesOrNoEnum;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/index")
@Api(value = "首页", tags = {"首页相关的接口"})
public class IndexController {
    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/carousel")
    @ApiOperation(value = "轮播图" , notes = "轮播图", httpMethod = "GET")
    public IMOOCJSONResult queryAll(){
        String carousel = redisOperator.get("carousel");
        List<Carousel> list = new ArrayList<>();
        if (StringUtils.isBlank(carousel)){
            list = carouselService.querryAll(YesOrNoEnum.YES.type);
            redisOperator.set("carousel", JsonUtils.objectToJson(list));
        }else {
            list = JsonUtils.jsonToList(carousel, Carousel.class);
        }
        return IMOOCJSONResult.ok(list);
    }

    @GetMapping("/cats")
    @ApiOperation(value = "获取商品分类（一级分类）" , notes = "获取商品分类（一级分类）", httpMethod = "GET")
    public IMOOCJSONResult queryAllRootLevelCat(){
        String cats = redisOperator.get("cats");
        List<Category> list = new ArrayList<>();
        if (null == cats || StringUtils.isBlank(cats)){
            list = categoryService.queryAllRootLevelCat(CategoryEnum.One.type);
            redisOperator.set("cats",JsonUtils.objectToJson(list));
        }else {
            list = JsonUtils.jsonToList(cats,Category.class);
        }
        return IMOOCJSONResult.ok(list);
    }

    @GetMapping("/subCat/{rootCatId}")
    @ApiOperation(value = "获取商品子分类（二级分类三级分类）" , notes = "获取商品子分类（二级分类三级分类", httpMethod = "GET")
    public IMOOCJSONResult querySubCat(
            @ApiParam(name = "rootCatId",value = "一级分类id",required = true)
            @PathVariable Integer rootCatId){
        if (rootCatId  == null){
            return IMOOCJSONResult.errorMsg("商品分类不存在");
        }
        List<CategoryVO> list = new ArrayList<>();
        String subCatStr = redisOperator.get("subCat:" + rootCatId);
        if (StringUtils.isBlank(subCatStr)){
            list = categoryService.getSubCatList(rootCatId);

            if(list != null && list.size() > 0){
                redisOperator.set("subCat:"+rootCatId,JsonUtils.objectToJson(list));
            }else {
                redisOperator.set("subCat:"+rootCatId,JsonUtils.objectToJson(list),5*60);
            }

        }else {
            list = JsonUtils.jsonToList(subCatStr,CategoryVO.class);
        }

        return IMOOCJSONResult.ok(list);
    }

    @GetMapping("/sixNewItems/{rootCatId}")
    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据" , notes = "查询每个一级分类下的最新6条商品数据", httpMethod = "GET")
    public IMOOCJSONResult sixNewItems(
            @ApiParam(name = "rootCatId",value = "一级分类id",required = true)
            @PathVariable Integer rootCatId){
        if (rootCatId  == null){
            return IMOOCJSONResult.errorMsg("商品分类不存在");
        }
        List<NewItemsVO> sixItem = categoryService.getSixNewItemsLazy(rootCatId);
        return IMOOCJSONResult.ok(sixItem);
    }


}
