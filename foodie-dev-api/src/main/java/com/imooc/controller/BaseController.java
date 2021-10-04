package com.imooc.controller;

import com.imooc.pojo.Orders;
import com.imooc.service.center.MyOrderService;
import com.imooc.utils.IMOOCJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;

@Controller
public class BaseController {
    public static final String FOODIE_SHOPCART = "shopcart";
    public static final Integer COMMON_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;
    public static final String IMAGE_USER_FACE_LOCATION = File.separator + "E:" + File.separator + "code" + File.separator + "image";
    String payReturnUrl = "http://1.117.214.155:8088/foodie-dev-api/orders/notifyMerchantOrderPaid";
    @Autowired
    private MyOrderService myOrderService;

    public IMOOCJSONResult checkUserOrder(String userId,String orderId){
        Orders order = myOrderService.queryMyOrder(userId, orderId);
        if (order == null) {
            return IMOOCJSONResult.errorMsg("订单不存在！");
        }
        return IMOOCJSONResult.ok(order);
    }
}
