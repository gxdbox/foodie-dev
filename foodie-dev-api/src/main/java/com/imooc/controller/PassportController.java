package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UsersService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("passport")
public class PassportController {
    @Autowired
    private UsersService usersService;

    final static Logger logger = LoggerFactory.getLogger(PassportController.class);

    @ApiOperation(value = "用户名是否已存在" , notes = "用户名是否已存在", httpMethod = "GET")
    @GetMapping(value = "/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username)  {
        if (StringUtils.isBlank(username)){
            return IMOOCJSONResult.errorMsg("username不能为空");
        }

        boolean isExist = usersService.queryUserIsExist(username);

        if (isExist){
            return IMOOCJSONResult.errorMsg("username已经存在");
        }

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户注册",notes = "用户注册",httpMethod = "POST")
    @PostMapping ("/regist")
    public IMOOCJSONResult createUser(@RequestBody UserBO userBO){
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String repassword = userBO.getConfirmPassword();

        //1、判空，参数不能为空
        if (StringUtils.isBlank(password) ||
                StringUtils.isBlank(repassword) ||
                StringUtils.isBlank(username) ){
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }

        //2、密码长度不能小于6位
        if (password.length()<6){
            return IMOOCJSONResult.errorMsg("密码长度不能小于6位");
        }

        //3、查询用户名是否存在
        boolean isExist = usersService.queryUserIsExist(username);
        if (isExist){
            return IMOOCJSONResult.errorMsg("username已经存在");
        }
        //4、两次密码不一致
        if (!password.equals(repassword)){
            return IMOOCJSONResult.errorMsg("两次输入的密码不一致");
        }
        //5、创建用户
        Users user = usersService.createUser(userBO);
        return IMOOCJSONResult.ok(user);
    }


    @ApiOperation(value = "用户登录",notes = "用户登录",httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult queryUserForLogin(@RequestBody UserBO userBO,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {


        if (StringUtils.isBlank(userBO.getUsername()) || StringUtils.isBlank(userBO.getPassword())){
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }

        Users userResult = usersService.queryUserForLogin(userBO.getUsername(), MD5Utils.getMD5Str(userBO.getPassword()));

        if (userResult == null){
            return IMOOCJSONResult.errorMsg("用户名或密码错误");
        }

        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(userResult),true);

        return IMOOCJSONResult.ok(userResult);
    }

    @ApiOperation(value = "用户退出",notes = "用户退出",httpMethod = "POST")
    @PostMapping("/logout")
    public IMOOCJSONResult logout(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String userId) throws Exception {
        CookieUtils.deleteCookie(request,response,"user");
        return IMOOCJSONResult.ok();
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }

}
