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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/index")
@Api(value = "首页", tags = {"首页相关的接口"})
public class IndexController {
    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/carousel")
    @ApiOperation(value = "轮播图" , notes = "轮播图", httpMethod = "GET")
    public IMOOCJSONResult queryAll(){
        List<Carousel> lists = carouselService.querryAll(YesOrNoEnum.YES.type);
        return IMOOCJSONResult.ok(lists);
    }

    @GetMapping("/cats")
    @ApiOperation(value = "获取商品分类（一级分类）" , notes = "获取商品分类（一级分类）", httpMethod = "GET")
    public IMOOCJSONResult queryAllRootLevelCat(){
        List<Category> categories = categoryService.queryAllRootLevelCat(CategoryEnum.One.type);
        return IMOOCJSONResult.ok(categories);
    }

    @GetMapping("/subCat/{rootCatId}")
    @ApiOperation(value = "获取商品子分类（二级分类三级分类）" , notes = "获取商品子分类（二级分类三级分类", httpMethod = "GET")
    public IMOOCJSONResult querySubCat(
            @ApiParam(name = "rootCatId",value = "一级分类id",required = true)
            @PathVariable Integer rootCatId){
        if (rootCatId  == null){
            return IMOOCJSONResult.errorMsg("商品分类不存在");
        }
        List<CategoryVO> categoryVOS = categoryService.getSubCatList(rootCatId);
        return IMOOCJSONResult.ok(categoryVOS);
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
