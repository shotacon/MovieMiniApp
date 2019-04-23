package com.shotacon.movie.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.alibaba.fastjson.JSON;
import com.shotacon.movie.api.moviefan.constant.OrderStatus;
import com.shotacon.movie.api.moviefan.entity.OrderEntity;
import com.shotacon.movie.api.moviefan.exception.BodyHandlerException;
import com.shotacon.movie.api.moviefan.service.MovieFanService;
import com.shotacon.movie.config.ErrorCode;
import com.shotacon.movie.exception.NoRangeInfoException;
import com.shotacon.movie.mapper.CouponMapper;
import com.shotacon.movie.mapper.PayMapper;
import com.shotacon.movie.mapper.TicketMapper;
import com.shotacon.movie.mapper.UserMapper;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.model.WXOrderDB;
import com.shotacon.movie.utils.newapi.SmsUtil;
import com.shotacon.movie.utils.old.DateUtil;
import com.shotacon.movie.utils.old.WXPay;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PayService {

    public static final String RETURN_PAY_SUCCESS_XML = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    public static final String RETURN_PAY_FAIL_XML = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[订单信息有误！]]></return_msg></xml>";

    // 【交易类型常量】
    /**
     * 再次支付
     */
    public static final String AGAIN_PAY = "payagain";

    // 【币种】
    /**
     * 人民币
     */
    public static final String CURRENCY_RMB = "CNY";

    // 【订单状态】
    /**
     * 初始状态
     */
    public static final String INITIAL = "WAIT";
    /**
     * 待发货(支付完成)
     */
    public static final String PENDING_SEND = "PENDING_SEND";
    /**
     * 卖家已发货
     */
    public static final String SELLER_SENT = "SELLER_SENT";
    /**
     * 交易成功 【结束状态】
     */
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    /**
     * 支付失败
     */
    public static final String PAY_FAILURE = "PAY_FAILURE";
    /**
     * 支付超时(24小时没支付) 【结束状态】
     */
    public static final String PAY_TIME_OUT = "PAY_TIME_OUT";
    /**
     * 交易关闭 【结束状态】
     */
    public static final String CLOSED = "CLOSED";
    /**
     * 退款中 【订单查询返回的状态】
     */
    public static final String REFUND = "REFUND";
    /**
     * 支付成功
     */
    public static final String PAY_SUCCESS = "PAY_SUCCESS";

    @Autowired
    UserMapper userMapper;
    @Autowired
    CouponMapper couponMapper;
    @Autowired
    TicketMapper ticketMapper;
    @Autowired
    PayMapper payMapper;
    @Autowired
    MovieFanService movieFanService;
    @Autowired
    TicketService ticketService;
    @Autowired
    SmsUtil smsUtil;

    /**
     * 检查优惠券
     * 
     * @param token
     * @param ticketMap
     * @param couponCodeList
     * @return
     * @throws NoRangeInfoException
     * @throws SQLException
     */
    public ResMsg checkCoupon(String token, Map<String, Object> ticketMap, List<String> couponCodeList)
            throws NoRangeInfoException, SQLException {

        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));

        String couponType = "";
        List<Map<String, Object>> couponList = new ArrayList<>();
        if (null != couponCodeList && couponCodeList.size() > 0) {
            for (String couponCode : couponCodeList) {
                List<Map<String, Object>> coupon = couponMapper.getCouponByCode(couponCode);
                if (null == coupon || coupon.size() <= 0) {
                    return ResMsg.fail(ErrorCode.COUPON_NO_PSW_ERROR, "卡号或密码错误,或已被绑定");
                }
                Map<String, Object> couponMap = coupon.get(0);
                String now = DateUtil.getNowDateStrByFormatStr(DateUtil.TOTAL_DATE_TIME);
                List<Map<String, Object>> discountRuleMsgByCouponCode = couponMapper
                        .getDiscountRuleMsgByCouponCode(couponCode, uid, now);
                String prefix = String.valueOf(discountRuleMsgByCouponCode.get(0).get("prefix"));
                couponMap.put("prefix", prefix);
                couponList.add(couponMap);
            }

            couponType = String.valueOf(couponList.get(0).get("prefix"));
            for (Map<String, Object> couponMap : couponList) {
                if (!couponMap.get("prefix").equals(couponType)) {
                    return ResMsg.fail(ErrorCode.COUPON_NOT_FOUND, "优惠类型不统一");
                }

                if (couponType.equals("REDBAG_")) {
                    if (couponList.size() > 1) {
                        return ResMsg.fail(ErrorCode.COUPON_NOT_FOUND, "红包最多可以用1张");
                    }
                } else {
                    Integer ticketCount = Integer.valueOf(String.valueOf(ticketMap.get("ticket_count")));
                    if (ticketCount < couponList.size() || couponList.size() > 4) {
                        return ResMsg.fail(ErrorCode.COUPON_NOT_FOUND, "通兑券最多可用4张，且不可超过购买影票数");
                    }
                }
            }

        }

        // 票数
        int ticketCount = Integer.valueOf(String.valueOf(ticketMap.get("ticket_count")));
        int length = String.valueOf(ticketMap.get("seat_ids")).split(",").length;
        // 单价
        BigDecimal unitPrice = new BigDecimal(String.valueOf(ticketMap.get("origin_price")));
        // 全价
        BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(ticketCount));

        log.info("Ticket count {}, seats count {}, unitPrice {}, totalAmount {}", ticketCount, length, unitPrice,
                totalAmount);

        return ResMsg.succWithData(
                calculateDiscount(ticketMap, totalAmount, unitPrice, String.valueOf(uid), couponCodeList, couponType));
    }

    /**
     * 创建微信订单
     * 
     * @param ticketMap
     * @param ip
     * @param couponCode
     * @param token
     * @param couponType
     * @throws SQLException
     * @throws NoRangeInfoException
     */
    public ResMsg createOrder(Map<String, Object> ticketMap, String openId, String ip, List<String> couponCode,
            String token) throws NoRangeInfoException, SQLException {

        ResMsg checkCoupon = checkCoupon(token, ticketMap, couponCode);
        // 折扣价
        BigDecimal c_price = new BigDecimal(String.valueOf(checkCoupon.getData()));
        // 商户订单号
        String orderNo = String.valueOf(ticketMap.get("order_no"));
        // 标价金额
        BigDecimal unityPrice = new BigDecimal(String.valueOf(ticketMap.get("origin_price")));
        BigDecimal ticketCount = new BigDecimal(String.valueOf(ticketMap.get("ticket_count")));
        BigDecimal totalAmount = unityPrice.multiply(ticketCount);
        // 优惠额度
        String amount = totalAmount.subtract(c_price).toString();
        // 商品描述
        String body = String.valueOf(ticketMap.get("film_name"));
        // 附加数据
        String attach = String.valueOf(ticketMap.get("order_no"));

        BigDecimal zero = new BigDecimal(0);
        log.info("折扣价: {}", c_price.toString());

        if (zero.compareTo(c_price) == 0) {
            log.info("价格为0, 0元付");
            ticketMapper.updateTicketStatusAndCouponByOrderNO(TicketService.STATUS_PAY_SUCCESS, orderNo,
                    StringUtils.join(couponCode, ","), amount, c_price.toString());
            Map<String, String> payZeroDeal = payZeroDeal(orderNo);
            ticketService.updateCoupon(orderNo, true);

            return ResMsg.succWithData(payZeroDeal);
        } else {
            // 保存记录
            saveWXOrderDB(ticketMap, openId);
            ticketMapper.updateTicketStatusAndCouponByOrderNO(TicketService.STATUS_PAY_ING, orderNo,
                    StringUtils.join(couponCode, ","), amount, c_price.toString());
            Map<String, String> unifiedOrder = WXPay.unifiedOrder(openId, orderNo, c_price.toString(), ip, body,
                    attach);
            return ResMsg.succWithData(unifiedOrder);
        }

    }

    private Map<String, String> payZeroDeal(String payFlowNo) throws SQLException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("total_fee", "0");
        map.put("result_code", "success");
        map.put("result_msg", "0元支付成功");
        map.put("successDatetime", DateUtil.getNowDateStrByFormatStr(DateUtil.TOTAL_DATE_TIME));
        map.put("payFlowNo", payFlowNo);
        map.put("payBank", "PAY_ZERO");
        Map<String, String> result = new HashMap<String, String>();
        result.put("payType", "zero");
        ticketService.submitOrder(payFlowNo);
        ticketService.updateCoupon(payFlowNo, true);
        return result;
    }

    /**
     * 保存微信订单记录
     * 
     * @param ticketMap
     * @param openId
     * @return
     */
    public WXOrderDB saveWXOrderDB(Map<String, Object> ticketMap, String openId) {

        WXOrderDB order = new WXOrderDB();
        order.setOrderNo(String.valueOf(ticketMap.get("order_no")));
        order.setAmount(String.valueOf(ticketMap.get("origin_price")));
        order.setBody(String.valueOf(ticketMap.get("film_name")));
        order.setOpenId(openId);
        order.setStatus(INITIAL);
        payMapper.saveWXOrderDB(order);
        return order;
    }

    /**
     * 计算折扣
     * 
     * @param record
     * @param totalAmount
     * @param unitPrice
     * @param uid
     * @param coupons
     * @param couponType
     * @return
     * @throws NoRangeInfoException
     * @throws SQLException
     */
    public BigDecimal calculateDiscount(Map<String, Object> record, BigDecimal totalAmount, BigDecimal unitPrice,
            String uid, List<String> coupons, String couponType) throws NoRangeInfoException, SQLException {
        log.info("\n\n\n-----------------------开始计算价格----------------------------------");
        log.info("原价:" + totalAmount.doubleValue());
        log.info("优惠券数量:" + coupons.size());

        if (coupons != null && coupons.size() != 0) {
            log.info("存在优惠信息，需要进行优惠计算");

            List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
            for (String couponCode : coupons) {
                log.info("轮询优惠信息");
                String now = DateUtil.getNowDateStrByFormatStr(DateUtil.TOTAL_DATE_TIME);
                List<Map<String, Object>> discountRuleList = couponMapper.getDiscountRuleMsgByCouponCode(couponCode,
                        Integer.valueOf(uid), now);
                Map<String, Object> ruleMap = discountRuleList.get(0);
                rules.add(ruleMap);
                log.info("该优惠信息的规则为：" + JSON.toJSONString(discountRuleList));

                String ruleRange = String.valueOf(ruleMap.get("range"));
                String cinemaId = String.valueOf(record.get("cinema_id"));
                String filmId = String.valueOf(record.get("film_id"));
                String cityCode = String.valueOf(record.get("city_code"));

                boolean checkRange = checkRange(ruleRange, cinemaId, filmId, cityCode);

                log.info("优惠是否可用：" + checkRange);
                if (!checkRange) {
                    log.info("该优惠不可使用，直接返回原价");
                    return totalAmount.setScale(2, RoundingMode.HALF_UP);
                }
            }

            if (couponType.equals("EXCHANGE_")) {
                log.info("当前优惠为通兑券");
                for (int i = 0; i < coupons.size(); i++) {
                    log.info("轮询通兑券");
                    String maximumPrice = String.valueOf(rules.get(i).get("maximum_price"));
                    BigDecimal maxPrice = BigDecimal.valueOf(Double.valueOf(maximumPrice));
                    log.info("通兑券最高兑换价：" + maxPrice.doubleValue() + " 当前订单单价：" + unitPrice.doubleValue());
                    if (maxPrice.compareTo(unitPrice) > 0) {
                        log.info("通兑券可用");
                        totalAmount = totalAmount.subtract(unitPrice).setScale(2, RoundingMode.HALF_UP);
                        log.info("改张通兑券使用后价格为：" + totalAmount.doubleValue());
                    } else {
                        log.info("通兑券不可用");
                        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
                        log.info("当前订单价格为：" + totalAmount.doubleValue());
                    }
                }
                log.info("计算完整后，订单价格为" + totalAmount.doubleValue());
                log.info("-----------------------计算价格结束----------------------------------\n\n\n");
                return totalAmount;
            } else {
                String maximumPrice = String.valueOf(rules.get(0).get("maximum_price"));
                BigDecimal minTotalPrice = BigDecimal.valueOf(Double.valueOf(maximumPrice));
                if (minTotalPrice.compareTo(totalAmount) > 0) {
                    return totalAmount.setScale(2, RoundingMode.HALF_UP);
                } else {
                    String couponCode = String.valueOf(coupons.get(0));
                    List<String> priceExpressionList = couponMapper.getPriceExpression(couponCode,
                            Integer.valueOf(uid));
                    if (null == priceExpressionList || priceExpressionList.size() <= 0) {
                        log.error("can not find priceExpression by coupon code : {}", couponCode);
                        throw new NoRangeInfoException();
                    }
                    String priceExpression = priceExpressionList.get(0);
                    Map<String, Object> paramValues = new HashMap<String, Object>();
                    paramValues.put("totalAmount", totalAmount);
                    String nowPriceStr = expressionCalculate(priceExpression, paramValues);
                    if (nowPriceStr != null) {
                        BigDecimal nowPrice = new BigDecimal(nowPriceStr);
                        if (nowPrice.compareTo(new BigDecimal("0.0")) > 0) {
                            BigDecimal maxDiscountAmount = minTotalPrice;
                            BigDecimal discountAmount = totalAmount.subtract(nowPrice);
                            if (discountAmount.compareTo(maxDiscountAmount) > 0) {
                                return totalAmount.subtract(maxDiscountAmount);
                            } else {
                                return nowPrice.setScale(2, RoundingMode.HALF_UP);
                            }
                        } else {
                            return new BigDecimal("0.0");
                        }
                    } else {
                        return null;
                    }
                }
            }
        } else {
            log.info("没有优惠信息，直接返回原价");
            log.info("-----------------------计算价格结束----------------------------------\n\n\n");
            return totalAmount.setScale(2, RoundingMode.HALF_UP);
        }
    }

    /**
     * 不知道检查什么
     * 
     * @param type
     * @param cinemaId
     * @param filmId
     * @param cityCode
     * @return
     * @throws SQLException
     */
    public boolean checkRange(String type, String cinemaId, String filmId, String cityCode) throws SQLException {
        if ("ALL".equals(type)) {
            return true;
        }
        Map<String, Object> rangeMap = null;
        try {
            List<Map<String, Object>> rangeList = couponMapper.getRangeInfo(type);
            if (null == rangeList || rangeList.size() <= 0) {
                throw new NoRangeInfoException();
            }

            rangeMap = rangeList.get(0);
        } catch (NoRangeInfoException e) {
            return false;
        }

        String cinemaIds = String.valueOf(rangeMap.get("cinema_id"));
        if (!"ALL".equals(cinemaIds)) {
            if (!cinemaIds.contains(cinemaId)) {
                return false;
            }
        }

        String filmIds = String.valueOf(rangeMap.get("film_id"));
        if (!"ALL".equals(filmIds)) {
            if (!filmIds.contains(filmId)) {
                return false;
            }
        }

        String cityCodes = String.valueOf(rangeMap.get("city_code"));
        if (!"ALL".equals(cityCodes)) {
            if (!cityCodes.contains(cityCode)) {
                return false;
            }
        }

        return true;
    }

    private String expressionCalculate(String expression, Map<String, Object> paramValues) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        Iterator<String> keys = paramValues.keySet().iterator();
        String key = null;
        while (keys.hasNext()) {
            key = keys.next();
            if (engine.get(key) == null) {
                engine.put(key, paramValues.get(key));
            }
        }
        try {
            Object calcValue = engine.eval(expression);
            return calcValue.toString();
        } catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResMsg queryOrderRealtime(String token, String orderExternalID) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        String uid = String.valueOf(userList.get(0).get("uid"));
        try {
            OrderEntity queryOrder = movieFanService.queryOrder(orderExternalID);
            Integer externalOrderStatus = queryOrder.getExternalOrderStatus();
            String status = "";
            String statusMsg = "";
            List<Map<String, Object>> ticketByOrderNo = ticketMapper.getTicketByAoolyKeyAndUid(orderExternalID, uid);
            if (externalOrderStatus == OrderStatus.SUCC.getStatus()) {
                status = TicketService.STATUS_TICKETING_SUCCESS;
                if (!status.equals(ticketByOrderNo.get(0).get("status"))) {
                    smsUtil.sendTicketSuccessMsg(orderExternalID, uid, ticketByOrderNo);
                }
                statusMsg = "已出票";
            } else if (externalOrderStatus == OrderStatus.FAIL.getStatus()) {
                status = TicketService.STATUS_TICKETING_FAIL;
                if (!status.equals(ticketByOrderNo.get(0).get("status"))) {
                    smsUtil.sendTicketErrorMsg(orderExternalID, uid, ticketByOrderNo);
                }
                ticketService.updateCoupon(String.valueOf(ticketByOrderNo.get(0).get("order_no")), false);
                statusMsg = "出票失败";
            } else if (externalOrderStatus == OrderStatus.CANCEL.getStatus()) {
                status = TicketService.STATUS_PAY_CANCEL;
                ticketService.updateCoupon(String.valueOf(ticketByOrderNo.get(0).get("order_no")), false);
                statusMsg = "取消支付";
            } else if (externalOrderStatus == OrderStatus.RELEASE.getStatus()) {
                status = TicketService.STATUS_UNLOCK_SUCCESS;
                ticketService.updateCoupon(String.valueOf(ticketByOrderNo.get(0).get("order_no")), false);
                statusMsg = "取消锁座成功";
            }
            // 如果支付成功, 且锁座状态, 认为出票中
            if (TicketService.STATUS_TICKETING_DOING.equals(ticketByOrderNo.get(0).get("status"))
                    && externalOrderStatus.equals(OrderStatus.LOCK_SUCC.getStatus())) {
                statusMsg = "出票中";
            }
            log.info("出票状态: {}, {}", status, statusMsg);
            log.info(queryOrder.toString());
            if (StringUtils.isNotEmpty(status)) {
                ticketMapper.updateTicketStatusAndQrCode(status, orderExternalID, queryOrder.getQrCode());
            }
            List<Map<String, Object>> ticketList = ticketMapper.getTicketByAoolyKeyAndUid(orderExternalID, uid);
            if (null == ticketList || ticketList.size() <= 0) {
                return ResMsg.succWithData(queryOrder);
            }
            Object c = ticketList.get(0).get("coupon_sub");
            BigDecimal couponSub = new BigDecimal(null == c ? "0" : String.valueOf(c));
            queryOrder.setCouponSub(couponSub.multiply(new BigDecimal(100)).toString());
            queryOrder.setTicketStatus(statusMsg);
            return ResMsg.succWithData(queryOrder);
        } catch (RestClientException | BodyHandlerException e) {
            log.error("Query Order Error: ", e);
            return ResMsg.fail(ErrorCode.NET_ERROR, e.getMessage());
        }
    }

    /**
     * 查询微信订单状态
     * 
     * @param token
     * @param orderExternalID
     * @return
     */
    public ResMsg queryOrderStatus(String token, String recordId) {

        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        String uid = String.valueOf(userList.get(0).get("uid"));

        List<Map<String, Object>> ticketList = ticketMapper.getTicketByOrderNoAndUid(recordId, uid);
        if (null == ticketList || ticketList.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_ORDER_ERROR, "该订单不存在");
        }

        String orderNo = String.valueOf(ticketList.get(0).get("order_no"));
        handleNotify(WXPay.orderquery(orderNo));

        List<Map<String, Object>> orderList = payMapper.queryWXOrderDB(orderNo);
        if (null == orderList || orderList.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_ORDER_ERROR, "该订单不存在");
        }
        return ResMsg.succWithData(orderList.get(0));
    }

    /**
     * 处理回调
     * 
     * @param map
     * @return
     */
    public boolean handleNotify(Map<String, String> map) {
        if (WXPay.RETURN_CODE_SUCCESS.equals(map.get("return_code"))) {
            String outTradeNo = String.valueOf(map.get("out_trade_no"));

            if (WXPay.RETURN_CODE_SUCCESS.equals(map.get("result_code")) && !"NOTPAY".equals(map.get("trade_state"))) {
//                BigDecimal totalFee = new BigDecimal(String.valueOf(map.get("total_fee"))).divide(new BigDecimal(100));

                // 查询订单
                List<Map<String, Object>> ticketList = ticketMapper.getTicketByOrderNo(outTradeNo);
                if (null == ticketList || ticketList.size() <= 0) {
                    log.info("未查询到订单, 无法校验订单有效性.");
                    return false;
                }
//                BigDecimal originPrice = new BigDecimal(String.valueOf(ticketList.get(0).get("origin_price")));
//                if (totalFee.compareTo(originPrice) != 0) {
//                    return false;
//                }

                // 更新电影票订单状态
//                ticketMapper.updateTicketStatusByOrderNO(PAY_SUCCESS, outTradeNo);

                // 更新微信订单状态
                payMapper.updateWXOrderDB(outTradeNo, PAY_SUCCESS);

                ticketService.updateCoupon(outTradeNo, true);

                return true;
            }
            String message = String.valueOf(map.get("return_msg"));
            payMapper.updateWXOrderDBFail(outTradeNo, PAY_FAILURE, message);
            ticketService.updateCoupon(outTradeNo, false);
            return false;
        } else {
            return false;
        }
    }

    public List<Map<String, Object>> queryPayingRecord(String status) {
        return payMapper.queryPayingRecord(status);
    }

    public List<Map<String, Object>> queryTicketByStatus(String statusTicketingDoing) {
        return payMapper.queryTicketByStatus(statusTicketingDoing);
    }

}
