package com.imooc.controller.center;

import com.imooc.pojo.Users;
import com.imooc.service.center.CenterUserService;
import com.imooc.service.center.MyOrderService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "用户中心", tags = "用户中心相关接口")
@RestController
@RequestMapping("center")
public class CenterController {
    @Autowired
    private CenterUserService centerUserService;
    @Autowired
    private MyOrderService myOrderService;

    @GetMapping("userInfo")
    @ApiOperation(value = "获取用户信息", tags = "查询用户中心", httpMethod = "GET")
    public IMOOCJSONResult userInfo(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam("userId") String userId) {

        Users users = centerUserService.queryUserInfo(userId);
        return IMOOCJSONResult.ok(users);
    }


}
