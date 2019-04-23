package com.shotacon.movie.utils.newapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.alibaba.druid.util.StringUtils;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.shotacon.movie.api.moviefan.entity.OrderEntity;
import com.shotacon.movie.api.moviefan.exception.BodyHandlerException;
import com.shotacon.movie.api.moviefan.service.MovieFanService;
import com.shotacon.movie.config.WXPayConstants;
import com.shotacon.movie.mapper.TicketMapper;
import com.shotacon.movie.utils.old.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SmsUtil {

    public static final String TPP_TICKETING_SUCCESS_WITH_PASSWORD = "SMS_122285175";
    public static final String TPP_TICKETING_SUCCESS = "SMS_122290144";
    public static final String TPP_TICKETING_ERROR = "SMS_122295040";

    @Autowired
    TicketMapper ticketMapper;
    @Autowired
    MovieFanService movieFanService;

    @SuppressWarnings("deprecation")
	public boolean sendSms(String mobile, String templateCode, String jsonStr) {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", WXPayConstants.DYSMS_ACCESS_KEY_ID,
                WXPayConstants.DYSMS_ACCESS_KEY_SECRET);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", WXPayConstants.DYSMS_PRODUCT,
                    WXPayConstants.DYSMS_DOMAIN);
        } catch (ClientException e) {
            e.printStackTrace();
            return false;
        }

        IAcsClient acsClient = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setMethod(MethodType.POST);
        request.setPhoneNumbers(mobile);
        request.setSignName("太电影");
        request.setTemplateCode(templateCode);
        request.setTemplateParam(jsonStr);
        SendSmsResponse response = null;
        try {
            response = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
            return false;
        }
        if (response.getCode() != null && response.getCode().equals("OK")) {
            return true;
        } else {
            System.out.println(response.getCode());
            System.out.println(response.getMessage());
            return false;
        }
    }

    public void sendTicketErrorMsg(String extUserId, String uid, List<Map<String, Object>> ticketByOrderNo) {
        try {
            if (null == ticketByOrderNo || ticketByOrderNo.size() <= 0) {
                log.info("未查询到该订单: {}", extUserId);
                return;
            }
            Map<String, Object> map = ticketByOrderNo.get(0);
            String mobile = String.valueOf(map.get("mobile"));
            String name = String.valueOf(map.get("film_name"));
            name = name.length() > 10 ? name.substring(0, 7) + ".." : name;

            Map<String, String> param = new HashMap<String, String>();
            param.put("name", name);
            boolean isSuccess = sendSms(mobile, TPP_TICKETING_ERROR, JsonUtils.objectToJson(param));
            if (isSuccess) {
                log.info("发送出票失败短信成功，手机号" + mobile);
            } else {
                log.info("发送出票失败短信失败，手机号" + mobile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void sendTicketSuccessMsg(String extUserId, String uid, List<Map<String, Object>> ticketByOrderNo) {
        if (null == ticketByOrderNo || ticketByOrderNo.size() <= 0) {
            log.info("未查询到该订单: {}", extUserId);
            return;
        }
        try {
            Map<String, Object> map = ticketByOrderNo.get(0);
            OrderEntity queryOrder;
            queryOrder = movieFanService.queryOrder(extUserId);

            String mobile = String.valueOf(map.get("mobile"));
            String name = String.valueOf(map.get("film_name"));

            Map<String, String> param = new HashMap<String, String>();

            name = name.length() > 10 ? name.substring(0, 7) + ".." : name;
            String date = queryOrder.getStartTime();
            String play = queryOrder.getHallName() + queryOrder.getSeatName();
            if (play.length() > 20) {
                play = play.substring(0, 18) + "……";
            }
            if (date.length() > 20) {
                date = date.substring(0, 18) + "……";
            }
            String templateCode = TPP_TICKETING_SUCCESS_WITH_PASSWORD;

            String content = StringUtils.isEmpty(queryOrder.getPrintCode()) ? "" : "出票号:" + queryOrder.getPrintCode();
            String pass = StringUtils.isEmpty(queryOrder.getVerifyCode()) ? "" : "取票密码:" + queryOrder.getVerifyCode();
            param.put("code", content + pass);
            param.put("name", name);
            param.put("date", date);
            param.put("play", play);
            templateCode = TPP_TICKETING_SUCCESS;
            boolean isSuccess = sendSms(mobile, templateCode, JsonUtils.objectToJson(param));

            if (isSuccess) {
                log.info("发送出票短信成功，手机号" + mobile);
            } else {
                log.info("发送出票短信失败，手机号" + mobile);
            }
        } catch (RestClientException | BodyHandlerException e) {
            log.error("未查询到相关订单", e);
            return;
        }
    }
}
