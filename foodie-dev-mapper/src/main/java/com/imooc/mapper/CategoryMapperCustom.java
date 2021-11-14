package com.imooc.mapper;

import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CategoryMapperCustom {
    List<CategoryVO> querySubCat(Integer rootCatId);

    List<NewItemsVO> getSixNewItems(@Param("paramsMap") Map<String, Object> map);
}