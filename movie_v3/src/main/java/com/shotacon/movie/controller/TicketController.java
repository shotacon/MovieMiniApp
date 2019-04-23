package com.shotacon.movie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.shotacon.movie.exception.ValidateException;
import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.service.TicketService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v2/api/")
@Api(tags = { "购票相关操作" })
public class TicketController extends BaseController {

    @Autowired
    private TicketService ticketService;

    @Value("${seats.lockTime}")
    private int seatsLockTime;

    @Value("${seats.dbLockTime}")
    private int seatsDBLockTime;

    @GetMapping("/seat/{scheduleId}")
    @ApiOperation(value = "获取座位图", notes = "获取座位图", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg querySeats(@PathVariable(value = "scheduleId", required = true) String scheduleId) {
        return ticketService.getSeat(scheduleId);
    }

    @PostMapping("/seat/release")
    @ApiOperation(value = "释放座位", notes = "释放座位座", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg releaseSeats(@RequestParam(value = "orderExternalID", required = true) String orderExternalID) {
        return ticketService.releaseSeats(orderExternalID);
    }

    @PostMapping("/seat/submit")
    @ApiOperation(value = "确认出票", notes = "确认出票", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg submitOrder(
            @ApiParam(value = "平台订单ID", name = "orderExternalID") @RequestParam(value = "orderExternalID", required = true) String orderExternalID,
            @ApiParam(value = "手机号", name = "mobile") @RequestParam(value = "mobile", required = true) String mobile,
            @ApiParam(value = "总价(分)", name = "amount") @RequestParam(value = "amount", required = true) String amount) {
        return ticketService.submitOrder(orderExternalID, mobile, amount);
    }

    /**
     * "scheduleId": "1041898621", //排期ID <br>
     * "seatIds": "38096071|38096072", //座位ID，用| 分隔，不允许隔空选座，即选1 3 ，跳过2<br>
     * "seatNames": "8排13座|8排14座", //座位名称，用|分隔，需要与ID匹配 <br>
     * "tipMessage": "您选择的是<b>周一 06-11 02:32</b>的场次，<b>后天晚上就要出发</b>，请看仔细哦" //购票须知
     * 
     * @param token
     * @param scheduleId
     * @param tipMessage
     * @param seatNames
     * @param seatIds
     * @return
     */
    @PostMapping("/seat/lock")
    @ApiOperation(value = "锁座", notes = "锁座", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg lockSeat(@RequestHeader(name = "Authorization") String token,
            @ApiParam(value = "排期ID", name = "scheduleId") @RequestParam(value = "scheduleId", required = true) String scheduleId,
            @ApiParam(value = "购票须知", name = "tipMessage") @RequestParam(value = "tipMessage", required = true) String tipMessage,
            @ApiParam(value = "座位名称, 例如: 8排13座|8排14座 , 用|分隔，需要与ID匹配 ", name = "seatNames") @RequestParam(value = "seatNames", required = true) String seatNames,
            @ApiParam(value = "座位ID, 例如: 38096071|38096072 , 用| 分隔，不允许隔空选座，即选1 3 ，跳过2 ", name = "seatIds") @RequestParam(value = "seatIds", required = true) String seatIds) {
        String key;
        try {
            key = forbidReLock(token, scheduleId);
        } catch (OperateLimitException e) {
            log.error("lockSeats error: ", e);
            return ResMsg.fail(ErrorCode.OPERATE_LIMIT_ERROR, e.getMessage());
        }
        redisUtil.set(key, seatIds, seatsLockTime);
        return ticketService.lockSeat(token, scheduleId, tipMessage, seatNames.replace("|", ","),
                seatIds.replace("|", ","), seatsDBLockTime);
    }

    @GetMapping("/ticket")
    @ApiOperation(value = "获取电影票列表", notes = "获取电影票列表", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg ticket(@RequestHeader(name = "Authorization") String token,
            @RequestParam(value = "pageIndex", defaultValue = "1") String pageIndex,
            @RequestParam(value = "pageNum", defaultValue = "6") String pageNum) {
        int page = 1;
        int num = 6;
        try {
            page = validateIntegerNullable(pageIndex, "pageIndex", 1);
            num = validateIntegerNullable(pageNum, "pageNum", 6);
        } catch (ValidateException e) {
            log.error("getTicket error: ", e);
            return ResMsg.fail(ErrorCode.PARAM_ERROR, e.getMessage());
        }
        return ticketService.getTicket(token, page, num);
    }

    @GetMapping("/ticket/byRecordId/{recordId}")
    @ApiOperation(value = "获取电影票详情", notes = "获取电影票详情", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg queryByRecordId(@RequestHeader(name = "Authorization") String token,
            @ApiParam(value = "平台订单号, 数据库字段为lock_seat_apply_key, 在部分接口里叫orderExternalID", name = "recordId") @PathVariable(value = "recordId", required = true) String recordId) {
        return ticketService.queryByRecordId(token, recordId);
    }

    @GetMapping("/ticket/byOrderNo/{orderNo}")
    @ApiOperation(value = "获取电影票详情", notes = "获取电影票详情", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg queryByOrderNo(@RequestHeader(name = "Authorization") String token,
            @ApiParam(value = "订单号, 数据库字段同名, 也是作为商户流水号参与支付的", name = "orderNo") @PathVariable(value = "orderNo", required = true) String orderNo) {
        return ticketService.queryByOrderNo(token, orderNo);
    }

}
