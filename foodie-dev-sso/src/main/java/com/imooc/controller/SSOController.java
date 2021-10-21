package com.imooc.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SSOController {
    final static Logger logger = LoggerFactory.getLogger(SSOController.class);

    @GetMapping("/login")
    @ResponseBody
    public String login(String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {
     model.addAttribute("returnUrl",returnUrl);
     //后续完善跳转判断 TODO
        return "login";
    }

}
