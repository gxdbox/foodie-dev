package com.imooc.controller;

import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.vo.ShopcartVO;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "购物车接口controller", tags = {"购物车接口相关的api"})
@RequestMapping("shopcart")
@RestController
public class ShopcatController extends BaseController{
    @Autowired
    private RedisOperator redisOperator;

    @PostMapping("add")
    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    public IMOOCJSONResult add(@RequestParam String userId,
                               @RequestBody ShopcartBO shopcartBO,
                               HttpServletRequest request,
                               HttpServletResponse response ){
        if (StringUtils.isBlank(userId)){
            IMOOCJSONResult.errorMsg("");
        }

        List<ShopcartBO> shopcartList = null;
        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        if (StringUtils.isNotBlank(shopcartJson)){
            // redis中已经有购物车了
            shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话counts累加
            Boolean isHaving = false;
            for (ShopcartBO sc : shopcartList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(shopcartBO.getSpecId())){
                    sc.setBuyCounts(sc.getBuyCounts() + shopcartBO.getBuyCounts());
                    isHaving = true;
                }
            }
            if (!isHaving){
                shopcartList.add(shopcartBO);
            }

        }else {
            // redis中没有购物车
            shopcartList = new ArrayList<>();
            shopcartList.add(shopcartBO);
        }
        redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartList));
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

        String shopcartRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        if (StringUtils.isNotBlank(shopcartRedis)){
            List<ShopcartBO> shopcartList = JsonUtils.jsonToList(shopcartRedis, ShopcartBO.class);

            for (ShopcartBO shopcartBO : shopcartList) {
                if (itemSpecId.equals(shopcartBO.getSpecId())){
                    shopcartList.remove(shopcartBO);
                    break;
                }
            }

            redisOperator.set(FOODIE_SHOPCART + ":" + userId,JsonUtils.objectToJson(shopcartList));
        }

        return IMOOCJSONResult.ok();
    }
}
