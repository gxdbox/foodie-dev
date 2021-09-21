package com.imooc.controller;

import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "购物车接口controller", tags = {"购物车接口相关的api"})
@RequestMapping("shopcart")
@RestController
public class ShopcatController {

    @PostMapping("add")
    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    public IMOOCJSONResult add(@RequestParam String userId,
                               @RequestBody ShopcartBO shopcartBO,
                               HttpServletRequest request,
                               HttpServletResponse response ){
        if (StringUtils.isBlank(userId)){
            IMOOCJSONResult.errorMsg("");
        }

        //将ShopcartBO购物车信息存入redis TODO
        System.out.println(shopcartBO);
        return IMOOCJSONResult.ok();
    }

    @PostMapping("del")
    @ApiOperation(value = "删除购物车商品", notes = "删除购物车商品", httpMethod = "POST")
    public IMOOCJSONResult add(@RequestParam String userId,
                               @RequestParam String itemSpecId,
                               HttpServletRequest request,
                               HttpServletResponse response ){
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)){
            IMOOCJSONResult.errorMsg("");
        }

        //将ShopcartBO购物车信息存入redis TODO
        return IMOOCJSONResult.ok();
    }
}
