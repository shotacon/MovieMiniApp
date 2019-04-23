package com.shotacon.movie.config;

public class WXPayConstants {

    /**
     * 到微信服务器换取微信用户身份id
     */
    public static final String JS2CODE_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 商户在小程序中先调用该接口在微信支付服务后台生成预支付交易单，返回正确的预支付交易后调起支付。
     */
    public static final String UNIFIEDORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 该接口提供所有微信支付订单的查询，商户可以通过查询订单接口主动查询订单状态，完成下一步的业务逻辑。<br>
     * 需要调用查询接口的情况：<br>
     * <li>当商户后台、网络、服务器等出现异常，商户系统最终未接收到支付通知；
     * <li>调用支付接口后，返回系统错误或未知交易状态情况；
     * <li>调用刷卡支付API，返回USERPAYING的状态；
     * <li>调用关单或撤销接口API之前，需确认支付状态；
     */
    public static final String ORDERQUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";

    /**
     * 以下情况需要调用关单接口：商户订单支付失败需要生成新单号重新发起支付，要对原订单号调用关单，避免重复支付；系统下单后，用户支付超时，系统退出不再受理，避免用户继续，请调用关单接口。
     * 
     * <br>
     * <b>注意：订单生成后不能马上调用关单接口，最短调用时间间隔为5分钟。
     */
    public static final String CLOSEORDER_URL = "https://api.mch.weixin.qq.com/pay/closeorder";

    public static final String WX_APP_ID = "wx3b198b1f04afff15";
    public static final String WX_APP_SECRET = "123";
    public static final String WX_GRANT_TYPE = "authorization_code";
    public static final String WX_PAY_APP_ID = "123";// todo
    public static final String WX_PAY_MCH_ID = "123";// todo
    public static final String WX_PAY_KEY = "123";// todo
    public static final String WX_PAY_NOTIFY_URL = "www.sss.ss";// todo
    public static final String WX_PAY_SIGNTYPE = "MD5";
    public static final String WX_PAY_TRADETYPE = "JSAPI";

    public static final String OPEN_API_KEY = "123";
    public static final String OPEN_API_SECRET = "123";
    
    public static final String DYSMS_PRODUCT = "Dysmsapi";
    public static final String DYSMS_DOMAIN = "dysmsapi.aliyuncs.com";
    public static final String DYSMS_ACCESS_KEY_ID = "123";
    public static final String DYSMS_ACCESS_KEY_SECRET = "123";

    public static final String BAIDU_MAP_AK = "123";

    public class PayStatus {
        /**
         * 初始状态
         */
        public static final String INITIAL = "WAIT";
        /**
         * 支付成功 【结束状态】
         */
        public static final String PAY_SUCCESS = "PAY_SUCCESS";
        /**
         * 【支付失败】出异常的交易 【结束状态】
         */
        public static final String PAY_FAILURE = "PAY_FAILURE";
        /**
         * 关闭 【结束状态】
         */
        public static final String CLOSED = "CLOSED";
        /**
         * 订单关闭，未支付【超时24小时未支付】 【结束状态】
         */
        public static final String PAY_TIME_OUT = "PAY_TIME_OUT";
        /**
         * 【支付成功】再次支付 新的流水 【结束状态】
         */
        public static final String SUCCESS_REPEAT_TRADE = "SUCCESS_REPEAT_TRADE";
        /**
         * 订单关闭之后支付流水成功 订单超时之后支付流水成功 【结束状态】
         */
        public static final String CLOSE_SUCCESS = "CLOSE_SUCCESS";
        /**
         * 退款中 【订单查询返回的状态】
         */
        public static final String REFUND = "REFUND";
    }

}
