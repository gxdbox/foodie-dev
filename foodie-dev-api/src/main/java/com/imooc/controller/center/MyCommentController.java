package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.enums.YesOrNoEnum;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.service.center.MyCommentService;
import com.imooc.service.center.MyOrderService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "用户中心评价模块", tags = {"用户中心评价模块相关接口"})
@RestController
@RequestMapping("mycomments")
public class MyCommentController extends BaseController {
    @Autowired
    private MyCommentService myCommentService;

    @ApiOperation(value = "我的待评价列表", notes = "我的待评价列表", httpMethod = "POST")
    @PostMapping("/pending")
    public IMOOCJSONResult query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId
            ) {
        IMOOCJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        Orders order = (Orders) checkResult.getData();

        if (order == null){
            return IMOOCJSONResult.errorMsg("订单不存在");
        }

        if (order.getIsComment() == YesOrNoEnum.YES.type){
            IMOOCJSONResult.errorMsg("订单已经评价");
        }
        List<OrderItems> list = myCommentService.queryPendingComments(orderId);

        return IMOOCJSONResult.ok(list);
    }

}
