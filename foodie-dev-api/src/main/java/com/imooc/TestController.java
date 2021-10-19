package com.imooc;

import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @GetMapping("getAlot")
    public List<String>  getAlot(String... keys){
        List<String> stringList = new ArrayList<>();
        for (String key : keys) {
            stringList.add(redisOperator.get(key));
        }
        return stringList;
    }

    @GetMapping("mget")
    public List<String>  mget(String... keys){
        List<String> list = Arrays.asList(keys);
        return redisOperator.mget(list);
    }
}
