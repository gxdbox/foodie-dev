package com.imooc.service.center;

import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.pojo.vo.MyCommentVO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface MyCommentService {
   List<OrderItems> queryPendingComments(String orderId);
   void saveComments(String userid, String orderId, List<OrderItemsCommentBO> commentBOS);
   PagedGridResult queryMyComments(String userId,Integer page,Integer pageSize);

}
