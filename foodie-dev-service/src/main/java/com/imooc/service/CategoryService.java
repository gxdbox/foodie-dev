package com.imooc.service;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;

import java.util.List;

public interface CategoryService {
    List<Category> queryAllRootLevelCat(Integer type);
    List<Category> queryAllSonLevelCat(Integer type);

}
