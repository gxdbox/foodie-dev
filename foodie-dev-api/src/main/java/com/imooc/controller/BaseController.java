package com.imooc.controller;

import com.imooc.pojo.Orders;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.center.MyOrderService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.UUID;

@Controller
public class BaseController {
    public static final String FOODIE_SHOPCART = "shopcart";
    public static final Integer COMMON_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String IMAGE_USER_FACE_LOCATION = File.separator + "E:" + File.separator + "code" + File.separator + "image";
    String payReturnUrl = "http://1.117.214.155:8088/foodie-dev-api/orders/notifyMerchantOrderPaid";
    @Autowired
    private MyOrderService myOrderService;
    @Autowired
    private RedisOperator redisOperator;

    public IMOOCJSONResult checkUserOrder(String userId,String orderId){
        Orders order = myOrderService.queryMyOrder(userId, orderId);
        if (order == null) {
            return IMOOCJSONResult.errorMsg("订单不存在！");
        }
        return IMOOCJSONResult.ok(order);
    }

    public UsersVO conventUserVO(Users user){
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user,usersVO);
        String uniqueToken = UUID.randomUUID().toString();
        redisOperator.set(REDIS_USER_TOKEN + ":" + usersVO.getId(),uniqueToken);
        usersVO.setToken(uniqueToken);
        return usersVO;
    }
}
