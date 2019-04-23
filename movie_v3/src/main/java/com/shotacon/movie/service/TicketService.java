package com.shotacon.movie.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.alibaba.fastjson.JSONObject;
import com.shotacon.movie.api.moviefan.entity.ShowSeatsEntity;
import com.shotacon.movie.api.moviefan.entity.ShowsEntity;
import com.shotacon.movie.api.moviefan.exception.BodyHandlerException;
import com.shotacon.movie.api.moviefan.mapper.MovieFanMapper;
import com.shotacon.movie.api.moviefan.util.MovieFanUtil;
import com.shotacon.movie.config.ErrorCode;
import com.shotacon.movie.mapper.CinemaMapper;
import com.shotacon.movie.mapper.CouponMapper;
import com.shotacon.movie.mapper.MovieMapper;
import com.shotacon.movie.mapper.TicketMapper;
import com.shotacon.movie.mapper.UserMapper;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.utils.old.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TicketService {

    public static final String STATUS_LOCK_SUCCESS = "LOCK_SUCCESS";// 锁座成功
    public static final String STATUS_LOCKING = "LOCKING";// 锁座中
    public static final String STATUS_LOCK_FAIL = "LOCK_FAIL";// 锁座失败
    public static final String STATUS_UNLOCK_SUCCESS = "UN_LOCK_SUCCESS";// 座位解锁成功
    public static final String STATUS_UNLOCK_FAIL = "UN_LOCK_FAIL";// 座位解锁失败
    public static final String STATUS_PAY_ING = "PAYING";// 正在支付
    public static final String STATUS_PAY_SUCCESS = "PAY_SUCCESS";// 支付成功
    public static final String STATUS_PAY_CANCEL = "PAY_CANCEL";// 正在支付
    public static final String STATUS_PAY_FAIL = "PAY_FAIL";// 支付失败
    public static final String STATUS_TICKETING_SUCCESS = "TICKETING_SUCCESS";// 出票成功
    public static final String STATUS_TICKETING_FAIL = "TICKETING_FAIL";// 出票失败
    public static final String STATUS_TICKETING_DOING = "TICKETING_DOING";// 出票中
    public static final String STATUS_TIKCET_RETRYING = "TICKETING_RETRYING";// 出票重试中

    @Autowired
    UserMapper userMapper;
    @Autowired
    MovieMapper movieMapper;
    @Autowired
    TicketMapper ticketMapper;
    @Autowired
    private CinemaMapper cinemaMapper;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    MovieFanMapper movieFanMapper;

    public ResMsg getSeat(String scheduleId) {
        try {
            List<Map<String, Object>> scheduleList = movieMapper.getScheduleById(scheduleId);
            if (null == scheduleList || scheduleList.size() <= 0) {
                log.info("未查询到该排期");
            } else {
                Map<String, Object> map = scheduleList.get(0);
                int cinemaId = Integer.parseInt(String.valueOf(map.get("cinemaId")));
                int filmId = Integer.parseInt(String.valueOf(map.get("filmId")));
                // 拉取最新排期
                List<ShowsEntity> queryShows = MovieFanUtil.queryShows(cinemaId, filmId);
                for (ShowsEntity showsEntity : queryShows) {
                    if (showsEntity.getShowtimeID() == Integer.parseInt(scheduleId)) {
                        movieFanMapper.updateShows(showsEntity);
                        log.info("拉取最新排期: {}", showsEntity.toString());
                    }
                }
            }
        } catch (Exception e1) {
            log.error("拉取最新排期错误: ", e1);
        }
        try {
            List<ShowSeatsEntity> queryShowSeats = MovieFanUtil.queryShowSeats(scheduleId, null);
            return ResMsg.succWithData(queryShowSeats);
        } catch (RestClientException | BodyHandlerException e) {
            log.error("queryShowSeats error: ", e);
            return ResMsg.fail(ErrorCode.NET_ERROR, e.getMessage());
        }
    }

    public List<Map<String, Object>> getTicketByOrderNo(String recordId) {
        return ticketMapper.getTicketByOrderNo(recordId);
    }

    public ResMsg lockSeat(String token, String scheduleId, String tipMessage, String seatNames, String seatIds,
            int seatsDBLockTime) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        String uid = String.valueOf(userList.get(0).get("uid"));
        String mobile = String.valueOf(userList.get(0).get("mobile"));

        List<Map<String, Object>> scheduleList = movieMapper.getScheduleById(scheduleId);
        if (null == scheduleList || scheduleList.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_SCHEDULE_ID, "无此排期");
        }
        Map<String, Object> scheduleMap = scheduleList.get(0);

        List<Map<String, Object>> cinemaList = cinemaMapper
                .getCinemaByCinemaId(String.valueOf(scheduleMap.get("cinemaId")));
        if (null == cinemaList || cinemaList.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_CINEMA_ID, "查无此影院");
        }
        Map<String, Object> cinemaMap = cinemaList.get(0);

        int ticketCount = seatIds.split(",").length;
        if (ticketCount > 4) {
            return ResMsg.fail(ErrorCode.CREATE_ORDER_ERROR, "最多锁定4个座位");
        }

        // 生成订单操作
        List<Map<String, Object>> ticketRecordList = initTicketRecord(uid, scheduleId, seatIds, seatNames, mobile,
                tipMessage, 1, null, scheduleMap, cinemaMap, ticketCount);
        if (null == ticketRecordList || ticketRecordList.size() <= 0) {
            return ResMsg.fail(ErrorCode.CREATE_ORDER_ERROR, "订单生成错误");
        }
        Map<String, Object> orderMap = ticketRecordList.get(0);
        String orderNo = String.valueOf(orderMap.get("order_no"));
        log.info("【锁座之前初始化电影票信息记录】-【成功】:extUserId" + orderMap.get("order_no"));

        // 锁座
        String orderExternalID = "";
        try {
            // 接口15分钟锁座时间
            Date date = DateUtil.getDateByAddSecond(15 * 60);
            orderExternalID = MovieFanUtil.lockSeats(scheduleId, seatIds, seatNames,
                    String.valueOf(userList.get(0).get("mobile")));

            // 更新订单
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.TOTAL_DATE_TIME);
            String expressTime = sdf.format(date);
            ticketMapper.updateLockSuccessBack(expressTime, orderExternalID, STATUS_LOCK_SUCCESS,
                    DateUtil.getNowDateStrByFormatStr(DateUtil.TOTAL_DATE_TIME), orderNo);
        } catch (RestClientException | BodyHandlerException e) {
            log.error("lockSeats error: ", e);
            return ResMsg.fail(ErrorCode.NET_ERROR, e.getMessage());
        }
        JSONObject result = new JSONObject();
        result.put("recordId", orderNo);
        result.put("orderNo", orderExternalID);
        log.info("锁座信息:{}", result.toJSONString());
        return ResMsg.succWithData(result);
    }

    public ResMsg getTicket(String token, int pageIndex, int pageNum) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));

        // 仅查询状态为10(锁座成功)和30(出票成功)
        List<Map<String, Object>> ticketList = ticketMapper.getTicket(uid, (pageIndex - 1) * pageNum, pageNum);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date());
        for (int i = 0; i < ticketList.size(); i++) {
            // 将过时的票标记为已使用
            if (String.valueOf(ticketList.get(i).get("schedule_start_time")).compareTo(now) < 0) {
                ticketList.get(i).put("used", true);
            } else {
                ticketList.get(i).put("used", false);
            }
        }
        return ResMsg.succWithData(ticketList);
    }

    private List<Map<String, Object>> initTicketRecord(String uid, String scheduleId, String seatIds, String seatNames,
            String mobile, String tipMessage, int recordType, String batchNo, Map<String, Object> scheduleMap,
            Map<String, Object> cinemaMap, int ticketCount) {

        String extUserId = getSerilNum();
        String city_code = String.valueOf(cinemaMap.get("cityId"));
        String city_name = String.valueOf(cinemaMap.get("cityName"));
        String film_id = String.valueOf(scheduleMap.get("filmId"));
        String film_name = String.valueOf(scheduleMap.get("filmName"));
        String cinema_id = String.valueOf(scheduleMap.get("cinemaId"));
        String cinema_name = String.valueOf(cinemaMap.get("cinema_name"));
        String schedule_id = String.valueOf(scheduleMap.get("scheduleId"));
        String schedule_date = String.valueOf(scheduleMap.get("showDate"));
        String schedule_start_time = String.valueOf(scheduleMap.get("showTime"));
        String schedule_end_time = String.valueOf(scheduleMap.get("closeTime"));
        String hall_name = String.valueOf(scheduleMap.get("hallName"));
        String create_time = DateUtil.getNowDateStrByFormatStr(DateUtil.TOTAL_DATE_TIME);
        String origin_price = String.valueOf(scheduleMap.get("price"));

        ticketMapper.addTicketRecord("NONE", city_code, city_name, uid, mobile, extUserId, film_id, film_name,
                cinema_id, cinema_name, schedule_id, schedule_date, schedule_start_time, schedule_end_time, hall_name,
                seatIds, seatNames, STATUS_LOCKING, 0, 0, tipMessage, "MF", recordType, create_time, ticketCount,
                batchNo, origin_price);

        return ticketMapper.getTicketByOrderNoAndUid(extUserId, uid);
    }

    private String getSerilNum() {
        String serilNum = "";
        int random = (int) ((Math.random() * 9) + 10);
        serilNum = String.valueOf(Math.abs(UUID.randomUUID().toString().hashCode()));
        String str = "0000000000";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmsssss");
        String nowStr = sdf.format(new Date());
        if (serilNum.length() > 10) {
            serilNum = serilNum.substring(0, 10);
        } else if (serilNum.length() < 10) {
            serilNum = serilNum + str.substring(serilNum.length());
        }
        serilNum = nowStr + random + serilNum;
        return serilNum;
    }

    public ResMsg releaseSeats(String orderExternalID) {
        try {
            JSONObject releaseSeats = MovieFanUtil.releaseSeats(orderExternalID);
            if ("1".equals(releaseSeats.getString("code"))) {
                ticketMapper.updateTicketStatus(STATUS_UNLOCK_SUCCESS, orderExternalID);
                return ResMsg.succ();
            } else {
                return ResMsg.UnknowWithMsg(releaseSeats.getString("msg"));
            }
        } catch (RestClientException | BodyHandlerException e) {
            log.error("releaseSeats error: ", e);
            return ResMsg.fail(ErrorCode.NET_ERROR, e.getMessage());
        }
    }

    public ResMsg submitOrder(String orderExternalID, String mobile, String amount) {
        try {
            MovieFanUtil.submitOrder(Integer.parseInt(amount), orderExternalID, mobile);
            ticketMapper.updateTicketStatus(STATUS_TICKETING_DOING, orderExternalID);
        } catch (RestClientException | BodyHandlerException e) {
            log.error("submitOrder error: ", e);
            return ResMsg.fail(ErrorCode.NET_ERROR, e.getMessage());
        }
        return ResMsg.succ();
    }

    public ResMsg queryByRecordId(String token, String recordId) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        String uid = String.valueOf(userList.get(0).get("uid"));
        return ResMsg.succWithData(ticketMapper.getTicketByAoolyKeyAndUid(recordId, uid));
    }

    public ResMsg queryByOrderNo(String token, String orderNo) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        String uid = String.valueOf(userList.get(0).get("uid"));
        return ResMsg.succWithData(ticketMapper.getTicketByOrderNoAndUid(orderNo, uid));
    }

    public void submitOrder(String outTradeNo) {
        try {
            List<Map<String, Object>> ticketByOrderNo = ticketMapper.getTicketByOrderNo(outTradeNo);
            if (null == ticketByOrderNo || ticketByOrderNo.size() <= 0) {
                return;
            }
            Map<String, Object> map = ticketByOrderNo.get(0);
            String orderExternalID = String.valueOf(map.get("lock_seat_apply_key"));
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(map.get("origin_price")));
            BigDecimal ticketCount = new BigDecimal(String.valueOf(map.get("ticket_count")));
            int intValue = bigDecimal.multiply(ticketCount).multiply(new BigDecimal(100)).intValue();
            MovieFanUtil.submitOrder(intValue, orderExternalID, String.valueOf(map.get("mobile")));
            ticketMapper.updateTicketStatus(STATUS_TICKETING_DOING, orderExternalID);
        } catch (RestClientException | BodyHandlerException e) {
            log.error("submitOrder error: ", e);
            return;
        }
    }

    public void updateCoupon(String outTradeNo, boolean isUse) {
        try {
            List<Map<String, Object>> ticketByOrderNo = ticketMapper.getTicketByOrderNo(outTradeNo);
            if (null == ticketByOrderNo || ticketByOrderNo.size() <= 0) {
                return;
            }
            Map<String, Object> map = ticketByOrderNo.get(0);
            String couponCodes = String.valueOf(map.get("coupon_code"));
            String uid = String.valueOf(map.get("uid"));
            String[] split = couponCodes.split(",");
            for (String couponCode : split) {
                if (isUse) {
                    couponMapper.updateCouponUsed(couponCode, uid);
                } else {
                    int i = 0;
                    List<Map<String, Object>> getticketByCouponCode = ticketMapper.getticketByCouponCode(couponCode);
                    for (Map<String, Object> ticket : getticketByCouponCode) {
                        if (TicketService.STATUS_TICKETING_SUCCESS.equals(ticket.get("status"))) {
                            i++;
                        }
                    }
                    if (i == 0) {
                        couponMapper.updateCouponUnUsed(couponCode, uid);
                    }
                }
            }
        } catch (Exception e) {
            log.error("更新优惠券状态失败: orderNo: {}, isUsed: {}", outTradeNo, isUse);
        }

    }

}
