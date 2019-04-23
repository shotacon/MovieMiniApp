package com.shotacon.movie.job;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.shotacon.movie.config.WXPayConstants.PayStatus;
import com.shotacon.movie.mapper.PayMapper;
import com.shotacon.movie.mapper.UserMapper;
import com.shotacon.movie.service.PayService;
import com.shotacon.movie.service.TicketService;
import com.shotacon.movie.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务
 */
@Slf4j
@Component
@EnableAsync
public class QueryPayStatusJob {

    @Autowired
    PayService payService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    PayMapper payMapper;
    @Autowired
    TicketService ticketService;
    @Autowired
    UserService userService;

//    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 1)
    public void queryOrderStatus() {
        log.info("轮询出票状态");
        // STATUS_TICKETING_DOING
        List<Map<String, Object>> ticketList = payService.queryTicketByStatus(TicketService.STATUS_TICKETING_DOING);
        if (null == ticketList || ticketList.size() <= 0) {
            log.info("未查询到需要确认状态的票, 结束job");
            return;
        }
        for (Map<String, Object> map : ticketList) {
            String orderExternalID = String.valueOf(map.get("lock_seat_apply_key"));
            String uid = String.valueOf(map.get("uid"));
            List<Map<String, Object>> userList = userService.getUserById(uid);
            String token = String.valueOf(userList.get(0).get("token"));
            payService.queryOrderRealtime(token, orderExternalID);
        }
        log.info("结束轮询出票状态");
    }

//    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 1)
    public void queryPayStatus() {

        log.info("QueryPayStatusJob Begin");

        List<Map<String, Object>> resultList = payService.queryPayingRecord(PayStatus.INITIAL);

        if (null == resultList || resultList.size() <= 0) {
            log.info("未查询到相关记录, 结束任务");
            return;
        }
        log.info("查询到{}条需要确认的记录", resultList.size());

        for (Map<String, Object> map : resultList) {
            String openId = String.valueOf(map.get("openId"));
            List<String> tokenList = userMapper.queryUserByOpenId(openId);
            if (null == tokenList || tokenList.size() <= 0) {
                log.info("未查询到openid为{}的记录", openId);
                continue;
            }
            String token = tokenList.get(0);
            String orderNo = String.valueOf(map.get("order_no"));
            payService.queryOrderStatus(token, orderNo);
            List<Map<String, Object>> queryWXOrderDB = payMapper.queryWXOrderDB(orderNo);
            if (queryWXOrderDB == null || queryWXOrderDB.size() <= 0) {
                log.info("未查询到支付订单");
                continue;
            }
            if (TicketService.STATUS_PAY_SUCCESS.equals(queryWXOrderDB.get(0).get("status"))) {
                ticketService.submitOrder(orderNo);
                ticketService.updateCoupon(orderNo, true);
            }
        }
        log.info("QueryPayStatusJob Done");
    }
}
