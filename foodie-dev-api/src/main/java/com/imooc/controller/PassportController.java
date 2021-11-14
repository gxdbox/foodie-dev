package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.bo.UserBO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UsersService;
import com.imooc.utils.*;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private RedisOperator redisOperator;

    final static Logger logger = LoggerFactory.getLogger(PassportController.class);

    @ApiOperation(value = "用户名是否已存在", notes = "用户名是否已存在", httpMethod = "GET")
    @GetMapping(value = "/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username) {
        if (StringUtils.isBlank(username)) {
            return IMOOCJSONResult.errorMsg("username不能为空");
        }

        boolean isExist = usersService.queryUserIsExist(username);

        if (isExist) {
            return IMOOCJSONResult.errorMsg("username已经存在");
        }

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/regist")
    public IMOOCJSONResult createUser(@RequestBody UserBO userBO,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String repassword = userBO.getConfirmPassword();

        //1、判空，参数不能为空
        if (StringUtils.isBlank(password) ||
                StringUtils.isBlank(repassword) ||
                StringUtils.isBlank(username)) {
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }

        //2、密码长度不能小于6位
        if (password.length() < 6) {
            return IMOOCJSONResult.errorMsg("密码长度不能小于6位");
        }

        //3、查询用户名是否存在
        boolean isExist = usersService.queryUserIsExist(username);
        if (isExist) {
            return IMOOCJSONResult.errorMsg("username已经存在");
        }
        //4、两次密码不一致
        if (!password.equals(repassword)) {
            return IMOOCJSONResult.errorMsg("两次输入的密码不一致");
        }
        //5、创建用户
        Users user = usersService.createUser(userBO);
        UsersVO usersVO = conventUserVO(user);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);

        synShopcartData(request, response, user.getId());
        return IMOOCJSONResult.ok(user);
    }


    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult queryUserForLogin(@RequestBody UserBO userBO,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {


        if (StringUtils.isBlank(userBO.getUsername()) || StringUtils.isBlank(userBO.getPassword())) {
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }

        Users userResult = usersService.queryUserForLogin(userBO.getUsername(), MD5Utils.getMD5Str(userBO.getPassword()));

        if (userResult == null) {
            return IMOOCJSONResult.errorMsg("用户名或密码错误");
        }
//        userResult = setNullProperty(userResult);
        UsersVO usersVO = conventUserVO(userResult);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);

        synShopcartData(request, response, userResult.getId());

        return IMOOCJSONResult.ok(userResult);
    }

    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     *
     * @param request
     * @param response
     */
    private void synShopcartData(HttpServletRequest request, HttpServletResponse response, String userId) {
        String shopcartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        String shopcartCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);
        List<ShopcartBO> pendingDeleteList = new ArrayList<>();
        if (StringUtils.isNotBlank(shopcartJsonRedis)) {
            if (StringUtils.isNotBlank(shopcartCookie)) {
                List<ShopcartBO> shopcartCookieList = JsonUtils.jsonToList(shopcartCookie, ShopcartBO.class);
                List<ShopcartBO> shopcartRedisList = JsonUtils.jsonToList(shopcartJsonRedis, ShopcartBO.class);

                for (ShopcartBO redisShopcart : shopcartRedisList) {
                    String redisSpecId = redisShopcart.getSpecId();
                    for (ShopcartBO cookieShopcart : shopcartCookieList) {
                        String cookieSpecId = cookieShopcart.getSpecId();
                        if (redisSpecId.equals(cookieSpecId)) {
                            // 覆盖购买数量，不累加，参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            // 把cookieShopcart放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }
                    }
                }

                // 从现有cookie中删除对应的覆盖过的商品数据
                shopcartCookieList.removeAll(pendingDeleteList);
                // 合并两个list
                shopcartRedisList.addAll(shopcartCookieList);
                // 更新到redis和cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartRedisList), true);
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartRedisList));

            } else {
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopcartJsonRedis, true);
            }
        } else {
            redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopcartCookie);
        }
    }

    @ApiOperation(value = "用户退出", notes = "用户退出", httpMethod = "POST")
    @PostMapping("/logout")
    public IMOOCJSONResult logout(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String userId) throws Exception {
        CookieUtils.deleteCookie(request, response, "user");
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
        CookieUtils.deleteCookie(request, response, FOODIE_SHOPCART);
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
