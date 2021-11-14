package com.imooc.controller;

import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("stu")
public class StuController {
    @Autowired
    private StuService stuService;

    @GetMapping("/getInfo")
    public Object getStuInfo(Integer id) {
        return stuService.getInfo(id);
    }

    @PostMapping("/saveStu")
    public Object saveStu() {
        Stu stu = new Stu();
        stu.setAge(18);
        stu.setName("tom");
        stuService.insert(stu);
        return "ok";
    }

    @PutMapping("/updateStu")
    public Object updateStu(Integer id) {
        Stu stu = new Stu();
        stu.setAge(18);
        stu.setName("tom");
        stu.setId(1203);
        stuService.updateStu(stu);
        return "ok";
    }

    @DeleteMapping("/deleteStu")
    public Object deleteStu(Integer id) {
        stuService.delete(id);
        return "ok";
    }

    @GetMapping("/setSession")
    public String setSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("userInfo", "new user");
        session.setMaxInactiveInterval(3600);
        session.getAttribute("userInfo");
        return "ok";
    }

}
