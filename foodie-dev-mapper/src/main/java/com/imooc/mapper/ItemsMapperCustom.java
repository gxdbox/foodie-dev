package com.imooc.mapper;

import com.imooc.my.mapper.MyMapper;
import com.imooc.pojo.Items;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.SearchItemVO;
import com.imooc.pojo.vo.ShopcartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsMapperCustom {
    List<ItemCommentVO> queryItemComment(@Param("paramsMap") Map<String, Object> map);

    List<SearchItemVO> search(@Param("paramsMap") Map<String, Object> map);

    List<SearchItemVO> searchByCat(@Param("paramsMap") Map<String, Object> map);

    List<ShopcartVO> queryItemsBySpecIds(@Param("paramsList") List<String> list);

    int decreaseItemStock(@Param("specId") String specId, @Param("buyCounts") int buyCounts);
}