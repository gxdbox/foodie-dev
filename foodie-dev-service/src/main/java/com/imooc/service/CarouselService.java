package com.imooc.service;

import com.imooc.pojo.Carousel;

import java.util.List;

public interface CarouselService {
    List<Carousel> querryAll(Integer isShow);
}
