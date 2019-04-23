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

import com.shotacon.movie.config.ErrorCode;
import com.shotacon.movie.exception.OperateLimitException;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.service.CouponService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.exceptions.JedisException;

@Slf4j
@RestController
@RequestMapping("/v2/api/")
@Api(tags = { "优惠卷们" })
public class CouponController extends BaseController {

    private static final String PREFIX_EXCHANGE = "EXCHANGE_";
    private static final String PREFIX_REDBAG = "REDBAG_";
//    private static final String CODE_STATUS_NOT_USE = "NOT_USE";

    @Autowired
    private CouponService couponService;

    @GetMapping("/coupon/redPacket")
    @ApiOperation(value = "获取红包列表", notes = "获取红包列表", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getAllCoupon(@RequestHeader(name = "Authorization") String token,
            @ApiParam(value = "选填, 默认为1", name = "pageIndex") @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
            @ApiParam(value = "选填, 默认为6", name = "pageNum") @RequestParam(value = "pageNum", defaultValue = "6", required = false) int pageNum) {
        return couponService.getAllCoupon(token, (pageIndex - 1) * pageNum, pageNum, PREFIX_REDBAG);
    }

    @GetMapping("/coupon/exchange")
    @ApiOperation(value = "获取通兑券列表", notes = "获取通兑券列表", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getAllExchange(@RequestHeader(name = "Authorization") String token,
            @ApiParam(value = "选填, 默认为1", name = "pageIndex") @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
            @ApiParam(value = "选填, 默认为6", name = "pageNum") @RequestParam(value = "pageNum", defaultValue = "6", required = false) int pageNum) {
        return couponService.getAllCoupon(token, (pageIndex - 1) * pageNum, pageNum, PREFIX_EXCHANGE);
    }

    @GetMapping("coupon/redPacket/usable/{recordId}")
    @ApiOperation(value = "当前订单可用的红包", notes = "当前订单可用的红包", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getUsableRedPacket(@RequestHeader(name = "Authorization") String token,
            @ApiParam(value = "订单号, 取自order_no, 必填", name = "recordId") @PathVariable(value = "recordId", required = true) String recordId,
            @ApiParam(value = "选填, 默认为1", name = "pageIndex") @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
            @ApiParam(value = "选填, 默认为6", name = "pageNum") @RequestParam(value = "pageNum", defaultValue = "6", required = false) int pageNum) {
        return couponService.getUsableCoupon(token, (pageIndex - 1) * pageNum, pageNum, recordId, PREFIX_REDBAG);
    }

    @GetMapping("coupon/exchange/usable/{recordId}")
    @ApiOperation(value = "当前订单可用的通兑券", notes = "当前订单可用的通兑券", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getUsableExchange(@RequestHeader(name = "Authorization") String token,
            @ApiParam(value = "订单号, 取自order_no, 必填", name = "recordId") @PathVariable(value = "recordId", required = true) String recordId,
            @ApiParam(value = "选填, 默认为1", name = "pageIndex") @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
            @ApiParam(value = "选填, 默认为6", name = "pageNum") @RequestParam(value = "pageNum", defaultValue = "6", required = false) int pageNum) {
        return couponService.getUsableCoupon(token, (pageIndex - 1) * pageNum, pageNum, recordId, PREFIX_EXCHANGE);
    }

    @PostMapping("/coupon/bind")
    @ApiOperation(value = "绑定优惠", notes = "绑定优惠", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg bindCoupon(@RequestHeader(name = "Authorization") String token,
            @ApiParam(value = "兑换编号, 必填", name = "cardNo") @RequestParam(value = "cardNo", required = true) String cardNo,
            @ApiParam(value = "兑换密码, 必填", name = "cardPassword") @RequestParam(value = "cardPassword", required = true) String cardPassword) {
        return couponService.bindCoupon(token, cardNo, cardPassword);
    }

    @PostMapping("/updateUsable")
    @ApiOperation(value = "取消订单后,恢复订单内优惠券", notes = "取消订单后,恢复订单内优惠券", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg updateUsable(@RequestHeader(name = "Authorization") String token,
            @RequestParam(value = "orderNo", required = true) String orderNo) {
        return couponService.updateUsable(token, orderNo);
    }

    @PostMapping("/isBind")
    @ApiOperation(value = "是否绑定优惠", notes = "是否绑定优惠", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg isBind(@RequestHeader(name = "Authorization") String token) {
        return couponService.isBind(token);
    }

    @PostMapping("/coupon/exchangeCard/used")
    @ApiOperation(value = "获取使用过的通兑卡", notes = "获取使用过的通兑卡", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg getUsedExchangeCard(@RequestHeader(name = "Authorization") String token,
            @ApiParam(value = "选填, 默认为1", name = "pageIndex") @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
            @ApiParam(value = "选填, 默认为6", name = "pageNum") @RequestParam(value = "pageNum", defaultValue = "6", required = false) int pageNum) {
        return couponService.getUsedExchangeCard(token, (pageIndex - 1) * pageNum, pageNum, PREFIX_EXCHANGE);
    }

    @PostMapping("/coupon/exchangeCard/use")
    @ApiOperation(value = "使用通兑卡兑换", notes = "使用通兑卡兑换", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg useExchangeCard(@RequestHeader(name = "Authorization") String token,
            @ApiParam(value = "兑换编号, 必填", name = "cardNo") @RequestParam(value = "cardNo", required = true) String cardNo,
            @ApiParam(value = "兑换密码, 必填", name = "cardPassword") @RequestParam(value = "cardPassword", required = true) String cardPassword) {
        String key = "";
        try {
            key = forbidUseCard(token, cardNo);
        } catch (JedisException | OperateLimitException e) {
            log.error("getTicket error: ", e);
            return ResMsg.fail(ErrorCode.OPERATE_LIMIT_ERROR, e.getMessage());
        }
        redisUtil.set(key, cardNo + token, 6);
        return couponService.useExchangeCard(token, cardNo, cardPassword);
    }
}
