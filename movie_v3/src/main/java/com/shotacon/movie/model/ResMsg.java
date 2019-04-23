package com.shotacon.movie.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
// 当参数为空时不参加序列化
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResMsg {

    private final static String SUCC_MSG = "ok";
    public final static int SUCC_CODE = 200;
    private final static int UNKNOW = 99999;

    private int code;

    private String errorMsg;

    private Object data;

    public static ResMsg succ() {
        return ResMsg.builder().code(SUCC_CODE).errorMsg(SUCC_MSG).build();
    }

    public static ResMsg succWithData(Object data) {
        return ResMsg.builder().code(SUCC_CODE).errorMsg(SUCC_MSG).data(data).build();
    }

    public static ResMsg fail(int failCode, String failMsg) {
        return ResMsg.builder().code(failCode).errorMsg(failMsg).build();
    }

    public static ResMsg UnknowWithMsg(String msg) {
        return ResMsg.builder().code(UNKNOW).errorMsg(msg).build();
    }

}
