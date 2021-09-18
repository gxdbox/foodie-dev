package com.imooc.service;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;

import java.util.List;

public interface CategoryService {
    List<Category> queryAllRootLevelCat(Integer type);
    List<CategoryVO>  getSubCatList(Integer rootCatId);
    List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId);

}
