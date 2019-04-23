package com.shotacon.movie.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shotacon.movie.config.ErrorCode;
import com.shotacon.movie.mapper.CouponMapper;
import com.shotacon.movie.mapper.TicketMapper;
import com.shotacon.movie.mapper.UserMapper;
import com.shotacon.movie.model.CouponCodeSeq;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.utils.old.AESUtil;
import com.shotacon.movie.utils.old.DateUtil;
import com.shotacon.movie.utils.old.StringUtil;

@Service
public class CouponService {

    private static final String STATUS_USED = "USED";
    private static final String STATUS_DELETED = "DELETED";
    private static final String STATUS_NOT_USE = "NOT_USE";

    @Autowired
    CouponMapper couponMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    TicketMapper ticketMapper;

    public ResMsg getAllCoupon(String token, int page, int num, String type) {

        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));

        return ResMsg.succWithData(couponMapper.getAllCoupon(uid, page, num, type));
    }

    public ResMsg getUsableCoupon(String token, int pageIndex, int pageNum, String recordId, String type) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));

        List<Map<String, Object>> recordList = ticketMapper.getTicketByOrderNo(recordId);
        if (null == recordList || recordList.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_RECORD_ERROR, "订单不存在");
        }
        Map<String, Object> record = recordList.get(0);

        // 计算价格
        // 获取票数
        int ticketCount = String.valueOf(record.get("seat_ids")).split("\\|").length;
        // 获取原价
        double unitPrice = Double.parseDouble(String.valueOf(record.get("origin_price")));
        // 总价
        double totalAmount = unitPrice * ticketCount;
        String cinemaId = String.valueOf(record.get("cinema_id"));
        String filmId = String.valueOf(record.get("film_id"));
        String cityCode = String.valueOf(record.get("city_code"));
        String now = DateUtil.getNowDateStrByFormatStr(DateUtil.TOTAL_DATE_TIME_NOT_SECOND);
        // 根据总价获取可用红包或通兑券
        List<Map<String, Object>> usableRedPacket = couponMapper.getUsableRedPacket(uid, totalAmount, cinemaId, filmId,
                cityCode, pageIndex, pageNum, now, type);
        return ResMsg.succWithData(usableRedPacket);
    }

    public ResMsg bindCoupon(String token, String cardNo, String cardPassword) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));
        List<String> exist = couponMapper.isExist(cardNo, cardPassword);
        if (null == exist || exist.size() <= 0) {
            return ResMsg.fail(ErrorCode.COUPON_NO_PSW_ERROR, "卡号或密码错误,或已被绑定");
        }
        if (!exist.get(0).equals("NOT_USE")) {
            return ResMsg.fail(ErrorCode.COUPON_CARD_HAD_USED, "此卡已被使用");
        }
        return ResMsg.succWithData(couponMapper.bindCoupon(uid, cardNo, cardPassword));
    }

    public ResMsg updateUsable(String token, String orderNo) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        String uid = String.valueOf(userList.get(0).get("uid"));
        // 订单相关
        List<Map<String, Object>> ticketList = ticketMapper.getTicketByOrderNoAndUid(orderNo, uid);
        if (null == ticketList || ticketList.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_ORDER_ERROR, "订单不存在");
        }
        Map<String, Object> ticketMap = ticketList.get(0);
        String couponCode = String.valueOf(ticketMap.get("coupon_code"));
        if (StringUtils.isEmpty(couponCode)) {
            return ResMsg.fail(ErrorCode.NO_COUPON_IN_ORDER, "订单内无优惠券使用信息");
        }

        return ResMsg.succWithData(couponMapper.updateCouponUnUsed(couponCode, uid));
    }

    public ResMsg isBind(String token) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));
        return ResMsg.succWithData(couponMapper.countCoupon(uid) >= 4 ? true : false);
    }

    public ResMsg getUsedExchangeCard(String token, int pageIndex, int pageNum, String prefixExchange) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));
        return ResMsg.succWithData(couponMapper.getUsedExchangeCard(uid, pageIndex, pageNum));
    }

    public ResMsg useExchangeCard(String token, String cardNo, String cardPassword) {

        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));

        // 获取通兑卡
        List<Map<String, Object>> exchangeCardList = couponMapper.getExchangeCard(cardNo);
        if (null == exchangeCardList || exchangeCardList.size() <= 0) {
            return ResMsg.fail(ErrorCode.COUPON_NO_PSW_ERROR, "卡号或密码错误,或已被绑定");
        }

        Map<String, Object> card = exchangeCardList.get(0);
        String status = String.valueOf(card.get("status"));

        if (STATUS_USED.equals(status)) {
            return ResMsg.fail(ErrorCode.EXCHANGE_CARD_HAD_USED, "该卡号已被使用");
        } else if (STATUS_DELETED.equals(status)) {
            return ResMsg.fail(ErrorCode.EXCHANGE_CARD_HAD_DELETED, "该卡已被删除");
        } else if (STATUS_NOT_USE.equals(status)) {
            String salt = String.valueOf(card.get("salt"));
            String saltPassword = AESUtil.encryptStr(cardPassword, salt);
            if (saltPassword != null && saltPassword.equals(card.get("password"))) {
                JSONArray jsonArray = JSON.parseArray(String.valueOf(card.get("content")));
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    long couponId = item.getLongValue("couponId");
                    List<Map<String, Object>> couponList = couponMapper.getCoupon(couponId);
                    if (couponList == null) {
                        return ResMsg.fail(ErrorCode.EXCHANGE_CARD_INFO_ERROR, "通兑卡信息有误！请联系客服");
                    }
                }

                String now = DateUtil.getNowDateStrByFormatStr(DateUtil.TOTAL_DATE_TIME);
                String usedBy = "usedBy:" + uid;

                couponMapper.updateExchangeCardUsed(uid, cardNo, now, usedBy);
                List<Map<String, Object>> data = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    long couponId = item.getLongValue("couponId");
                    int num = item.getIntValue("couponNum");
                    Map<String, Object> coupon = couponMapper.getCoupon(couponId).get(0);

                    Set<String> codeStringSet = new HashSet<String>(num);
                    for (int i1 = 0; i1 < num; i1++) {
                        String code = String.valueOf(System.currentTimeMillis() / 1000)
                                + StringUtil.getRandomStrByLength(4);
                        codeStringSet.add(code);
                    }
                    while (codeStringSet.size() < num) {
                        String code = String.valueOf(System.currentTimeMillis() / 1000)
                                + StringUtil.getRandomStrByLength(4);
                        codeStringSet.add(code);
                    }
                    Iterator<String> it = codeStringSet.iterator();

                    while (it.hasNext()) {
                        CouponCodeSeq a = new CouponCodeSeq();
                        couponMapper.genCouponCodeSeq(a);
                        long id = a.getId();
                        Map<String, Object> code = new HashMap<String, Object>();
                        code.put("codeNum", "T" + StringUtil.addZeroToStr(String.valueOf(id), 11));
                        code.put("couponId", couponId);
                        code.put("memberId", uid);
                        code.put("code", it.next());
                        code.put("status", "NOT_USE");
                        code.put("createBy", "CN:" + cardNo);
                        code.put("creationDate", new Date());
                        code.put("deleteFlag", false);
                        code.put("beginDate", coupon.get("begin_date"));
                        code.put("endDate", coupon.get("end_date"));
                        data.add(code);
                    }
                }

                couponMapper.addCouponCode(data);
            } else {
                return ResMsg.fail(ErrorCode.EXCHANGE_CARD_PASSWORD_ERROR, "密码错误");
            }
        } else {
            return ResMsg.fail(ErrorCode.EXCHANGE_CARD_OTHER, "卡状态验证失败");
        }

        return ResMsg.succ();
    }

}
