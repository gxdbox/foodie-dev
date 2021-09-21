package com.imooc.controller;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayEnum;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.service.OrdersService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "订单相关", tags = {"订单相关的api接口"})
@RequestMapping("/orders")
@RestController
public class OrdersController extends BaseController{
    @Autowired
    private OrdersService ordersService;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public IMOOCJSONResult create( @RequestBody SubmitOrderBO submitOrderBO,
                                   HttpServletRequest request,
                                   HttpServletResponse response){
        if (submitOrderBO.getPayMethod() != PayEnum.WEIXIN.type &&
                submitOrderBO.getPayMethod() !=PayEnum.ZHIFUBAO.type){
            return IMOOCJSONResult.errorMsg("不支持该支付方式");
        }


            //1、创建订单
        System.out.println(submitOrderBO.toString());
        String orderId = ordersService.create(submitOrderBO);
        //2、删除购物车当中的商品
//        CookieUtils.setCookie(request,response,FOODIE_SHOPCART,"",true);
            //3、向支付中心发起商品支付请求
        return IMOOCJSONResult.ok(orderId);
    }

    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(@RequestParam String orderId){
        ordersService.updateOrderStatus(orderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }
}
