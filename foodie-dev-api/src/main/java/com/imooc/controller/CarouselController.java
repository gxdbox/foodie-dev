package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Carousel;
import com.imooc.service.CarouselService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/index")
@Api(value = "首页", tags = {"首页相关的接口"})
public class CarouselController {
    @Autowired
    private CarouselService carouselService;

    @GetMapping("/carousel")
    @ApiOperation(value = "轮播图" , notes = "轮播图", httpMethod = "GET")
    public IMOOCJSONResult queryAll(){
        List<Carousel> lists = carouselService.querryAll(YesOrNo.YES.type);
        return IMOOCJSONResult.ok(lists);
    }
}
