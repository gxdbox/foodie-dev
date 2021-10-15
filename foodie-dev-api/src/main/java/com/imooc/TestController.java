package com.imooc;

import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("redis")
public class TestController {
    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("get")
    public String  get(String key){
        return (String) redisOperator.get(key);
    }

    @GetMapping("set")
    public String  set(String key,String  value){
        redisOperator.set(key,value);
        return "ok";
    }

    @GetMapping("del")
    public String  del(String key){
        redisOperator.del(key);
        return "ok";
    }

}
