package com.imooc.mapper;

import com.imooc.my.mapper.MyMapper;
import com.imooc.pojo.Category;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.vo.MyOrdersVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderMapperCustom extends MyMapper<MyOrdersVO> {
    public List<MyOrdersVO> queryMyOrders(@Param("paramsMap") Map<String,Object> map);
    public int queryMyOrderStatusCounts(@Param("paramsMap") Map<String,Object> map);
    public List<OrderStatus> getMyOrderTrend(@Param("paramsMap") Map<String, Object> map);
}