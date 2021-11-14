package com.imooc.controller;


import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UsersService;
import com.imooc.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class SSOController {
    final static Logger logger = LoggerFactory.getLogger(SSOController.class);
    private static final String REDIS_USER_TOKEN = "redis_user_token";
    private static final String REDIS_USER_TICKET = "redis_user_ticket";
    private static final String REDIS_TMP_TICKET = "redis_tmp_ticket";
    private static final String REDIS_USER_COOKIE = "redis_user_cookie";


    @Autowired
    private UsersService usersService;
    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        model.addAttribute("returnUrl", returnUrl);

        String userTicket = getCookie(request, REDIS_USER_COOKIE);
        boolean isVerified = verifyUserTicket(userTicket);
        if (isVerified) {
            String tmpTicket = createTmpTicket();
            return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
        }

        return "login";
    }

    private boolean verifyUserTicket(String userTicket) {
        // 0. 验证CAS门票不能为空
        if (StringUtils.isBlank(userTicket)) {
            return false;
        }

        // 1. 验证CAS门票是否有效
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return false;
        }

        String userToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userToken)) {
            return false;
        }

        return true;
    }

    @PostMapping("/doLogin")
    public String doLogin(String username,
                          String password,
                          String returnUrl,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) throws Exception {

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            model.addAttribute("errmsg", "用户名或密码不能为空");
            return "login";
        }

        Users userResult = usersService.queryUserForLogin(username,
                MD5Utils.getMD5Str(password));

        if (userResult == null) {
            model.addAttribute("errmsg", "用户名或密码错误");
            return "login";
        }

        String uniqueToken = UUID.randomUUID().toString().trim();
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult, usersVO);
        usersVO.setToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + usersVO.getId(), JsonUtils.objectToJson(usersVO));

        //3、
        String userTicket = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TICKET + ":" + userTicket, userResult.getId());

        //3、1
        setCookie(REDIS_USER_COOKIE, userTicket, response);

        //4、
        String tmpTicket = createTmpTicket();


        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
    }


    private String createTmpTicket() {
        String tmpTicket = UUID.randomUUID().toString().trim();
        try {
            redisOperator.set(REDIS_TMP_TICKET + ":" + tmpTicket, MD5Utils.getMD5Str(tmpTicket), 6000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpTicket;
    }

    private void setCookie(String key, String val, HttpServletResponse response) {
        Cookie cookie = new Cookie(key, val);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public IMOOCJSONResult verifyTmpTicket(String tmpTicket,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        //1、tmpTikcet
        String tmpTicketValue = redisOperator.get(REDIS_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(tmpTicketValue)) {
            return IMOOCJSONResult.errorMsg("用户票据异常");
        }

        if (!tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))) {
            return IMOOCJSONResult.errorMsg("用户票据异常");
        } else {
            redisOperator.del(REDIS_TMP_TICKET + ":" + tmpTicket);
        }

        //2、userTicket
        String userTicket = getCookie(request, REDIS_USER_COOKIE);
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("用户id为空");
        }

        String userToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userToken)) {
            return IMOOCJSONResult.errorMsg("用户session为空");
        }

        return IMOOCJSONResult.ok(JsonUtils.jsonToPojo(userToken, UsersVO.class));
    }

    private String getCookie(HttpServletRequest request, String tmpTicketValue) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || StringUtils.isBlank(tmpTicketValue)) {
            return null;
        }

        String cookieValue = null;
        for (Cookie cookie : cookies) {
            if (tmpTicketValue.equals(cookie.getName())) {
                cookieValue = cookie.getValue();
                break;
            }
        }
        return cookieValue;
    }

    @PostMapping("/logout")
    @ResponseBody
    public IMOOCJSONResult logout(String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        //1 0. 获取CAS中的用户门票
        String userTicket = getCookie(request, REDIS_USER_COOKIE);
        delCookie(response, REDIS_USER_COOKIE);

        // 2 清除redis的userTicket
        redisOperator.del(REDIS_USER_TICKET + ":" + userTicket);

        //3 清除redis userToken
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
        return IMOOCJSONResult.ok();
    }

    private void delCookie(HttpServletResponse response, String redisUserCookie) {
        Cookie cookie = new Cookie(redisUserCookie, null);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }


}
