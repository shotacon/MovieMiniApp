package com.shotacon.movie.api.moviefan.entity;

import com.shotacon.movie.utils.old.DateUtil;

import lombok.ToString;

@ToString
//@Table(name = "tihe_moviefan_order")
public class OrderEntity {

    private long id;

    // (name = "orderexternal_id", nullable = false)
    private String orderExternalID; // 平台订单号

    // (name = "create_time", nullable = false)
    private String createTime; // 下单时间（格式: yyyy-MM-dd HH:mm:ss）

    // (name = "quantity", nullable = false)
    private Integer quantity; // 数量

    // (name = "amount", nullable = false)
    private Double amount; // 订单金额

    // (name = "print_code", nullable = false)
    private String printCode; // 取票号

    // (name = "verify_code", nullable = false)
    private String verifyCode; // 取票验证码[可能为空]

    // (name = "pay_state", nullable = false)
    private Integer payState; // 支付状态(1：已支付0：未支付)

    // (name = "external_order_status", nullable = false)
    private Integer externalOrderStatus; // 订单状态(0:新建,2:删除,10:锁座成功,20:锁座失败,30:成功,40:失败,100:已取消,110:释放座位)

    // (name = "movie_name", nullable = false)
    private String movieName; // 影片名称

    // (name = "start_time", nullable = false)
    private String startTime; // 开场时间（例如"\/Date(1503475200000+0800)\/"）

    // (name = "cinema_name", nullable = false)
    private String cinemaName; // 影院名称

    // (name = "seat_name", nullable = false)
    private String seatName; // 座位名称

    // (name = "hall_name", nullable = false)
    private String hallName; // 影厅名称

    // (name = "cinema_address", nullable = false)
    private String cinemaAddress; // 影院地址

    // (name = "end_time", nullable = false)
    private String endTime; // 结束时间 同开场时间

    // (name = "returned_order_status", nullable = false)
    private Integer ReturnedOrderStatus; // 退款状态（0：未退单,1：供应商退单,2：票友退单,3：渠道退单）

    // (name = "take_ticket_position", nullable = false)
    private String TakeTicketPosition; // 取票位置（可能为空）

    // (name = "cinema_ticket_code", nullable = false)
    private String cinemaTicketCode; // 柜台取票码[可能为空] (可能含有竖线，若无表示取票时直接使用全部即可；若有竖线前为订单号，后为验证码) 例如”895463|505604”

    private String qrCode;

    private String couponSub;

    private String ticketStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderExternalID() {
        return orderExternalID;
    }

    public void setOrderExternalID(String orderExternalID) {
        this.orderExternalID = orderExternalID;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPrintCode() {
        return printCode;
    }

    public void setPrintCode(String printCode) {
        this.printCode = printCode;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public Integer getPayState() {
        return payState;
    }

    public void setPayState(Integer payState) {
        this.payState = payState;
    }

    public Integer getExternalOrderStatus() {
        return externalOrderStatus;
    }

    public void setExternalOrderStatus(Integer externalOrderStatus) {
        this.externalOrderStatus = externalOrderStatus;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = DateUtil.handlerDateFromMF(startTime);
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public String getCinemaAddress() {
        return cinemaAddress;
    }

    public void setCinemaAddress(String cinemaAddress) {
        this.cinemaAddress = cinemaAddress;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = DateUtil.handlerDateFromMF(endTime);
    }

    public Integer getReturnedOrderStatus() {
        return ReturnedOrderStatus;
    }

    public void setReturnedOrderStatus(Integer returnedOrderStatus) {
        ReturnedOrderStatus = returnedOrderStatus;
    }

    public String getTakeTicketPosition() {
        return TakeTicketPosition;
    }

    public void setTakeTicketPosition(String takeTicketPosition) {
        TakeTicketPosition = takeTicketPosition;
    }

    public String getCinemaTicketCode() {
        return cinemaTicketCode;
    }

    public void setCinemaTicketCode(String cinemaTicketCode) {
        this.cinemaTicketCode = cinemaTicketCode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getCouponSub() {
        return couponSub;
    }

    public void setCouponSub(String couponSub) {
        this.couponSub = couponSub;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

}