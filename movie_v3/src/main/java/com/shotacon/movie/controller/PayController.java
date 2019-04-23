package com.shotacon.movie.controller;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shotacon.movie.config.ErrorCode;
import com.shotacon.movie.exception.NoRangeInfoException;
import com.shotacon.movie.exception.OperateLimitException;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.service.PayService;
import com.shotacon.movie.service.TicketService;
import com.shotacon.movie.utils.newapi.IPUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.exceptions.JedisException;

@Slf4j
@RestController
@RequestMapping("/v2/api/")
@Api(tags = { "支付" })
public class PayController extends BaseController {

    @Autowired
    private PayService payService;
    @Autowired
    private TicketService ticketService;

    @PostMapping("/order/price")
    @ApiOperation(value = "预计算订单价格", notes = "预计算订单价格", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg price(@RequestHeader(name = "Authorization") String token,
            @ApiParam(value = "订单ID", name = "recordId") @RequestParam(value = "recordId", required = true) String recordId,
            @ApiParam(value = "优惠券码, 例如: 123123,456456,789789", name = "couponCode") @RequestParam(value = "couponCode", defaultValue = "", required = false) List<String> couponCode) {

        List<Map<String, Object>> ticketList = ticketService.getTicketByOrderNo(recordId);
        if (null == ticketList || ticketList.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_ORDER_ERROR, "查无此单");
        }
        try {
            return payService.checkCoupon(token, ticketList.get(0), couponCode);
        } catch (NoRangeInfoException | SQLException e) {
            return ResMsg.fail(ErrorCode.NO_COUPON_RANGE_ERROR, "预计算订单价格错误");
        }
    }

    @PostMapping("/order/create")
    @ApiOperation(value = "创建订单并调用支付", notes = "创建订单并调用支付", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg createOrder(@RequestHeader(name = "Authorization") String token,
            @ApiParam(value = "订单号", name = "recordId") @RequestParam(value = "recordId", required = true) String recordId,
            @RequestParam(value = "openId", required = true) String openId,
            @ApiParam(value = "优惠券码, 例如: 123123,456456,789789", name = "couponCode") @RequestParam(value = "couponCode", defaultValue = "") List<String> couponCode,
            HttpServletRequest request) {

        List<Map<String, Object>> ticketList = ticketService.getTicketByOrderNo(recordId);
        if (null == ticketList || ticketList.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_ORDER_ERROR, "查无此单");
        }
        Map<String, Object> ticketMap = ticketList.get(0);

        String key = "";
        try {
            key = forbidOrder(String.valueOf(ticketMap.get("order_no")));
        } catch (JedisException | OperateLimitException e) {
            e.printStackTrace();
        }
        redisUtil.set(key, recordId, 3);

        // 调用支付
        try {
            return payService.createOrder(ticketMap, openId, IPUtil.getIpAddr(request), couponCode, token);
        } catch (NoRangeInfoException | SQLException e) {
            log.error("createOrder error : ", e);
            return ResMsg.UnknowWithMsg(e.getMessage());
        }
    }

    @GetMapping("/order/realtime")
    @ApiOperation(value = "从第三方查询订单出票情况(实时), 1分钟查一次哦.", notes = "从第三方查询订单出票情况(实时), 1分钟查一次哦.", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg queryOrderRealtime(@RequestHeader(name = "Authorization", required = true) String token,
            @ApiParam(value = "平台订单ID", name = "orderExternalID") @RequestParam(value = "orderExternalID", required = true) String orderExternalID) {
        return payService.queryOrderRealtime(token, orderExternalID);
    }

    @GetMapping("/order/status")
    @ApiOperation(value = "查询微信支付状态", notes = "查询微信支付状态", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg queryOrderStatus(@RequestHeader(name = "Authorization", required = true) String token,
            @ApiParam(value = "订单id", name = "recordId") @RequestParam(value = "recordId", required = true) String recordId) {
        return payService.queryOrderStatus(token, recordId);
    }

    @PostMapping("/wxpay/notify")
    @ApiOperation(value = "微信支付结果推送", notes = "微信支付结果推送", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String payNotify(HttpServletRequest request) {
        InputStream in = null;
        try {
            in = request.getInputStream();

            SAXReader reader = new SAXReader();
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            Document doc = reader.read(in);
            Element root = doc.getRootElement();
            @SuppressWarnings("unchecked")
            List<Element> list = root.elements();
            Map<String, String> map = new HashMap<String, String>();
            for (Element element : list) {
                map.put(element.getName(), element.getText());
            }
            log.info("接收到推送: {}", map);

            if (payService.handleNotify(map)) {
                String outTradeNo = String.valueOf(map.get("out_trade_no"));
                ticketService.submitOrder(outTradeNo);
                return PayService.RETURN_PAY_SUCCESS_XML;
            }
            return PayService.RETURN_PAY_FAIL_XML;
        } catch (Exception e) {
            log.error("payNotify error: ", e);
            return PayService.RETURN_PAY_FAIL_XML;
        }
    }

}
