package com.shotacon.movie.model;

import lombok.ToString;

import lombok.Data;

@Data
@ToString
public class WXOrderDB {
    // 商户订单号
    String orderNo;
    // 标价金额
    String amount;
    // 商品描述
    String body;
    // 订单状态
    String status;
    // openId
    String openId;
    
    String updateTime;
    String createTime;
}
