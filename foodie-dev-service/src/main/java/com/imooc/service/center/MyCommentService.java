package com.imooc.service.center;

import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface MyCommentService {
   List<OrderItems> queryPendingComments(String orderId);
}
