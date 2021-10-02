package com.imooc.controller;

import org.springframework.stereotype.Controller;

import java.io.File;

@Controller
public class BaseController {
    public static final String FOODIE_SHOPCART = "shopcart";
    public static final Integer COMMON_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;
    public static final String IMAGE_USER_FACE_LOCATION = File.separator + "E:" + File.separator + "code" + File.separator + "image";
}
