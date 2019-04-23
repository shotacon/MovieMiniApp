package com.shotacon.movie.api.moviefan.constant;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;

/**
 * 订单状态
 * 
 * @author shotacon
 *
 */
@AllArgsConstructor
public enum OrderStatus {

    // 初始化订单状态
    NEW_ORDER(0, "新建订单"), DELETE(2, "删除"), LOCK_SUCC(10, "锁座成功"), LOCK_FAIL(20, "锁座失败"), SUCC(30, "成功"), FAIL(40, "失败"),
    CANCEL(100, "已取消"), RELEASE(110, "释放座位");

    private int status;

    private String message;

    public static String toMessage(int status) {
        for (OrderStatus bt : values()) {
            if (bt.getStatus() == status) {
                return bt.getMessage();
            }
        }
        return StringUtils.EMPTY;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
