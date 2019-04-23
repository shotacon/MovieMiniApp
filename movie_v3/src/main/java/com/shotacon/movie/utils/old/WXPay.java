package com.shotacon.movie.utils.old;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import com.shotacon.movie.config.WXPayConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WXPay {

    public final static String RETURN_CODE_SUCCESS = "SUCCESS";
    public final static String RETURN_CODE_FAIL = "FAIL";

    /**
     * 商户在小程序中先调用该接口在微信支付服务后台生成预支付交易单，返回正确的预支付交易后调起支付。
     * 
     * @param openId
     * @param orderNo
     * @param amount
     * @param spbillCreateIp
     * @param body
     * @param attach
     * @return
     */
    public static Map<String, String> unifiedOrder(String openId, String orderNo, String amount, String spbillCreateIp,
            String body, String attach) {

        log.info("发起预支付交易单");

        String mchId = WXPayConstants.WX_PAY_MCH_ID;
        String key = WXPayConstants.WX_PAY_KEY;
        String tradeType = WXPayConstants.WX_PAY_TRADETYPE;
        String nonceStr = PayUtils.getNonceStr();
        String notifyUrl = WXPayConstants.WX_PAY_NOTIFY_URL;

        String appId = WXPayConstants.WX_PAY_APP_ID;
        String totalFee = PayUtils.getMoney(amount);
//        totalFee = "1";// todo 调试用，支付1分钱

        Map<String, String> paraTemp = new HashMap<String, String>();
        paraTemp.put("appid", appId);
        paraTemp.put("body", body);
        paraTemp.put("mch_id", mchId); // 商户号
        paraTemp.put("nonce_str", nonceStr);
        paraTemp.put("notify_url", notifyUrl); // 支付结果回调接口
        paraTemp.put("openid", openId);
        paraTemp.put("out_trade_no", orderNo); // 商户订单号
        paraTemp.put("spbill_create_ip", spbillCreateIp); // 终端IP
        paraTemp.put("total_fee", totalFee);
        paraTemp.put("trade_type", tradeType); // 交易类型

        Map<String, String> map = PayUtils.paraFilter(paraTemp);
        String mapStr = PayUtils.createLinkString(map);
        String sign = PayUtils.sign(mapStr, key, "utf-8").toUpperCase();

        log.info("签名: {}", sign);

        StringBuffer xml = new StringBuffer("<xml>");
        xml.append("<appid>").append(appId).append("</appid>");
        xml.append("<body>").append(body).append("</body>");
        xml.append("<mch_id>").append(mchId).append("</mch_id>");
        xml.append("<nonce_str>").append(nonceStr).append("</nonce_str>");
        xml.append("<notify_url>").append(notifyUrl).append("</notify_url>");
        xml.append("<openid>").append(openId).append("</openid>");
        xml.append("<out_trade_no>").append(orderNo).append("</out_trade_no>");
        xml.append("<spbill_create_ip>").append(spbillCreateIp).append("</spbill_create_ip>");
        xml.append("<total_fee>").append(totalFee).append("</total_fee>");
        xml.append("<trade_type>").append(tradeType).append("</trade_type>");
        xml.append("<sign>").append(sign).append("</sign>");
        xml.append("</xml>");

        log.info("xml报文: {}", xml.toString());

        String result = HttpUtil.httpRequest(WXPayConstants.UNIFIEDORDER_URL, "POST", xml.toString());

        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String prepay_id = "";

        try {
            prepay_id = PayUtils.getPayNo(result);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        String packages = "prepay_id=" + prepay_id;
        String nonceStr1 = PayUtils.getNonceStr();
        String mapStr1 = "appId=" + appId + "&nonceStr=" + nonceStr1 + "&package=prepay_id=" + prepay_id
                + "&signType=MD5&timeStamp=" + timeStamp;
        String paySign = PayUtils.sign(mapStr1, key, "utf-8").toUpperCase();

        Map<String, String> resultMap = new HashMap<String, String>();
        Map<String, String> resultXMLMap = StringUtil.doXMLParse(result);
        resultMap.put("return_code", resultXMLMap.get("return_code"));
        resultMap.put("payType", "wx");
        resultMap.put("appId", appId);
        resultMap.put("timeStamp", timeStamp);
        resultMap.put("nonceStr", nonceStr1);
        resultMap.put("package", packages);
        resultMap.put("signType", "MD5");
        resultMap.put("paySign", paySign);
        log.info("再次签名: {}", resultMap);
        return resultMap;
    }

    public static Map<String, String> orderquery(String orderNo) {
        log.info("查询微信支付订单");
        // 计算签名
        String nonceStr = PayUtils.getNonceStr();
        Map<String, String> paraTemp = new HashMap<String, String>();
        paraTemp.put("appid", WXPayConstants.WX_PAY_APP_ID);
        paraTemp.put("mch_id", WXPayConstants.WX_PAY_MCH_ID); // 商户号
        paraTemp.put("out_trade_no", orderNo); // 商户订单号
        paraTemp.put("nonce_str", nonceStr);

        Map<String, String> map = PayUtils.paraFilter(paraTemp);
        String mapStr = PayUtils.createLinkString(map);
        String sign = PayUtils.sign(mapStr, WXPayConstants.WX_PAY_KEY, "utf-8").toUpperCase();

        log.info("签名: {}", sign);

        StringBuffer xml = new StringBuffer("<xml>");
        xml.append("<appid>").append(WXPayConstants.WX_PAY_APP_ID).append("</appid>");
        xml.append("<mch_id>").append(WXPayConstants.WX_PAY_MCH_ID).append("</mch_id>");
        xml.append("<nonce_str>").append(nonceStr).append("</nonce_str>");
        xml.append("<out_trade_no>").append(orderNo).append("</out_trade_no>");
        xml.append("<sign>").append(sign).append("</sign>");
        xml.append("</xml>");

        log.info("xml报文: {}", xml.toString());

        String result = HttpUtil.httpRequest(WXPayConstants.ORDERQUERY_URL, "POST", xml.toString());

        return StringUtil.doXMLParse(result);

    }
}
