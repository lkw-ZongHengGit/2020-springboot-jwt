package com.chilly.controller;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chilly.entity.User;
import com.chilly.service.UserService;
import com.chilly.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chilly Cui on 2020/9/9.
 */
@RestController
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/user/login")
    public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response,  User user) {
        log.info("用户名：{}", user.getName());

        Map<String, Object> map = new HashMap();

        try {
            User userDB = userService.login(user);

            Map<String, String> payload = new HashMap<>();
            payload.put("id", userDB.getId());
            payload.put("name", userDB.getName());
            String token = JWTUtils.getToken(payload);

            map.put("state", true);
            map.put("msg", "登录成功");
            map.put("token", token);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state", false);
            map.put("msg", e.getMessage());
            map.put("token", "");
        }

        return map;
    }

    @PostMapping("/user/test")
    public Map<String, Object> test(HttpServletRequest request) {
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        String name = verify.getClaim("name").asString();
        log.info("用户id：{}", id);
        log.info("用户名: {}", name);

        //TODO 业务逻辑
        Map<String, Object> map = new HashMap<>();
        map.put("state", true);
        map.put("msg", "请求成功");
        return map;
    }


    //自己突发奇想,把一些参数放入cookie里.然后验证个人想法
    @GetMapping("/user/logins")
    public Map<String, Object> logins(HttpServletRequest request, HttpServletResponse response,  User user) {
        log.info("用户名：{}", user.getName());

        Map<String, Object> map = new HashMap();
        Cookie cookie;
        try {

            User userDB = userService.login(user);

            Map<String, String> payload = new HashMap<>();
            payload.put("id", userDB.getId());
            payload.put("name", userDB.getName());
            String token = JWTUtils.getToken(payload);

            map.put("state", true);
            map.put("msg", "登录成功");
            map.put("token", token);

            /*
            * 改造的代码
            * */

            Cookie[] cookies = request.getCookies();
            cookie = new Cookie("token",token);
            cookie.setPath("/");
            cookie.setMaxAge(48*60*60);
            response.addCookie(cookie);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state", false);
            map.put("msg", e.getMessage());
            map.put("token", "");
        }

        return map;
    }


    @GetMapping("/user/tests")
    public Map<String, Object> tests(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
       Cookie cookie = cookies[0];
        String token = cookie.getValue();
        DecodedJWT verify = JWTUtils.verify(token);
        String id = verify.getClaim("id").asString();
        String name = verify.getClaim("name").asString();
        log.info("用户id：{}", id);
        log.info("用户名: {}", name);

        //TODO 业务逻辑
        Map<String, Object> map = new HashMap<>();
        map.put("state", true);
        map.put("msg", "请求成功");
        return map;
    }


}
