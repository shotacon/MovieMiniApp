package com.shotacon.movie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.service.UserService;
import com.shotacon.movie.utils.newapi.WXUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v2/api/")
@Api(tags = { "用户相关接口" })
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/unionId/{code}")
    @ApiOperation(value = "获取微信UnionId", notes = "获取微信UnionId", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getUnionId(@PathVariable(value = "code", required = true) String code) {
        log.info("getUnionId");
        JSONObject json = WXUtil.jscode2Session(code);
        return userService.insertUser(json.getString("openid"));
//        return userService.insertUser("wxasdasdasd");
    }

    @PostMapping("/user/code")
    @ApiOperation(value = "发送验证码", notes = "发送验证码", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg sendCode(@RequestParam(value = "phone", required = true) String phone) {
        return userService.sendCode(phone);
    }

    @PostMapping("/user/bind")
    @ApiOperation(value = "绑定手机号", notes = "绑定手机号", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg bindUser(@RequestParam(value = "phone", required = true) String phone,
            @RequestParam(value = "code", required = true) String code,
            @RequestHeader(name = "Authorization", required = true) String token) {
        return userService.bindUser(phone, code, token);
    }

    @GetMapping("/user")
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getUser(@RequestHeader(name = "Authorization", required = true) String token) {
        return userService.getUser(token);
    }

    @PostMapping("/checkIn")
    @ApiOperation(value = "用户签到", notes = "用户签到", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg checkIn(@RequestHeader(name = "Authorization", required = true) String token) {
        return userService.checkIn(token);
    }

    @PostMapping("/user/update")
    @ApiOperation(value = "更新用户信息", notes = "更新用户信息", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg updateUser(@RequestHeader(name = "Authorization", required = true) String token,
            @ApiParam(value = "微信昵称（选填）", name = "username") @RequestParam(value = "username", required = false) String username,
            @ApiParam(value = "头像地址（选填）", name = "avatarUrl") @RequestParam(value = "avatarUrl", required = false) String avatarUrl,
            @ApiParam(value = "性别（选填）", name = "sex") @RequestParam(value = "sex", required = false) String sex,
            @ApiParam(value = "用户区域（选填）", name = "area") @RequestParam(value = "area", required = false) String area) {
        return userService.updateUser(token, username, avatarUrl, sex, area);
    }

}
