package com.imooc.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@RestController
public class ItemsController {
    final static Logger logger = LoggerFactory.getLogger(ItemsController.class);

    @GetMapping("/hello")
    public Object hello() {

        return "Hello ElasticSearch~";
    }
}
