package com.shotacon.movie.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import com.shotacon.movie.config.ErrorCode;
import com.shotacon.movie.exception.ValidateCodeException;
import com.shotacon.movie.mapper.UserMapper;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.utils.newapi.WXUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public ResMsg insertUser(String openid) {

        log.info("Add Member And User With OpenId: {}", openid);

        userMapper.insertCommonMemberByUsername(openid);
        List<Map<String, Object>> commonMemberList = userMapper.queryCommonMemberByUsername(openid);
        if (null == commonMemberList || commonMemberList.size() <= 0) {
            ResMsg.fail(ErrorCode.SQL_ERROR, "数据库错误, save CommonMember error");
        }
        // 计算token
        String token = DigestUtils.sha1Hex(DigestUtils.md5Hex(openid + "xcx") + "xcx");
        String uid = String.valueOf(commonMemberList.get(0).get("uid"));

        userMapper.addUser(openid, token, uid);
        Map<String, String> result = new HashMap<String, String>();
        result.put("token", token);
        result.put("openId", openid);
        return ResMsg.succWithData(result);
    }

    public ResMsg sendCode(String phone) {
        // 验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        userMapper.saveCode(phone, code);

        // 发送验证码
        try {
            WXUtil.sendCode(phone, code);
        } catch (ValidateCodeException e) {
            log.error("Phone {} send code error: {}", phone, e.getMessage());
            return ResMsg.fail(ErrorCode.CODE_SEND_ERROR, "发送验证码失败");
        }
        log.info("Send code {} to phone {}", code, phone);
        return ResMsg.succ();
    }

    public ResMsg bindUser(String phone, String code, String token) {
        if (userMapper.validateCode(phone, code) <= 0) {
            return ResMsg.fail(ErrorCode.CODE_ERROR, "验证码错误或者已过期");
        }
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));

        if (userMapper.updateUserByPhone(phone, uid) <= 0) {
            return ResMsg.fail(ErrorCode.SQL_ERROR, "数据库错误");
        }
        return ResMsg.succ();
    }

    public ResMsg getUser(String token) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        Map<String, Object> userMap = userList.get(0);
        userMap.put("username", new String(Base64Utils.decodeFromString(String.valueOf(userMap.get("username")))));
        return ResMsg.succWithData(userMap);
    }

    public ResMsg checkIn(String token) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        Map<String, Object> user = userList.get(0);
        if (Boolean.parseBoolean(String.valueOf(user.get("check_in")))) {
            return ResMsg.fail(ErrorCode.HAD_CHECKED_IN, "今日已签到");
        }
        // 签到
        int uid = Integer.valueOf(String.valueOf(user.get("uid")));
        userMapper.checnIn(uid);
        int pointToday = Integer.valueOf(String.valueOf(user.get("point_today")));
        int point = 0;
        if (pointToday <= 48) {
            point = 2;
        } else if (pointToday == 49) {
            point = 1;
        } else {
            point = 0;
        }
        userMapper.updatePoint(uid, point, pointToday);
        userMapper.addRecord(uid, point, "每日签到");

        return ResMsg.succWithData(userMapper.queryUserByToken(token));
    }

    public ResMsg updateUser(String token, String username, String avatarUrl, String sex, String area) {
        if (StringUtils.isAllBlank(avatarUrl, sex, area, username)) {
            return ResMsg.succ();
        }
        username = StringUtils.isEmpty(username) ? username : Base64Utils.encodeToString(username.getBytes());
        return ResMsg.succWithData(userMapper.updateUserByToken(token, username, avatarUrl, sex, area));
    }

    public List<Map<String, Object>> getUserById(String uid) {
       return userMapper.getUserById(uid);
    }

}
