package com.shotacon.movie.config;

public class ErrorCode {
    public static final int SQL_ERROR = 10000;
    public static final int PARAM_ERROR = 10001;
    public static final int TOKEN_ERROR = 10002;
    public static final int NET_ERROR = 10003;
    public static final int JEDIS_ERROR = 10004;
    public static final int OPERATE_LIMIT_ERROR = 10005;
    public static final int OTHER_ERROR = 10006;
    public static final int CODE_SEND_ERROR = 10007;
    public static final int CODE_ERROR = 10008;

    public static final int HAD_CHECKED_IN = 20001;

    public static final int NO_CITY_NAME = 30001;
    public static final int NO_CITY_AREA = 30002;
    public static final int NO_CITY_INFO = 30003;

    public static final int NO_CINEMA_ID = 40001;

    public static final int NO_FILM_ID = 50001;
    public static final int WAS_MARKED = 50002;

    public static final int HAD_COMMENTED = 60001;

    public static final int NO_SCHEDULE_ID = 70001;

    public static final int NO_RECORD_ERROR = 80001;
    public static final int NO_ORDER_ERROR = 80002;
    public static final int NO_COUPON_IN_ORDER = 80003;

    public static final int NO_COUPON_RANGE_ERROR = 90001;
    public static final int EXCHANGE_CARD_NOT_FOUND = 90002;
    public static final int EXCHANGE_CARD_HAD_USED = 90003;
    public static final int EXCHANGE_CARD_HAD_DELETED = 90004;
    public static final int EXCHANGE_CARD_PASSWORD_ERROR = 90005;
    public static final int EXCHANGE_CARD_INFO_ERROR = 90006;
    public static final int EXCHANGE_CARD_OTHER = 90007;
    public static final int COUPON_NO_PSW_ERROR = 90008;
    public static final int COUPON_CARD_HAD_USED = 90009;
    public static final int COUPON_NOT_FOUND = 90010;

    public static final int CREATE_ORDER_ERROR = 11001;

    public static final int UNLOCK_SEAT_ERROR = 12001;

    public static final int NO_ADDRESS_ERROR = 13001;

    public static final int POINT_NOT_ENOUGH = 14001;
    public static final int STOCK_NOT_ENOUGH = 14002;
    public static final int GOODS_NOT_EXIST = 14003;
}
