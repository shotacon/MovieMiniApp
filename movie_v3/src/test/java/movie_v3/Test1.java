package movie_v3;

import java.math.BigDecimal;

public class Test1 {

    public static void main(String[] args) {
//        String json = "{\"msg\":\"操作成功\",\"code\":1,\"orderdetail\":{\"amount\":5000,\"quantity\":2,\"verifyCode\":\"\",\"printCode\":\"\",\"TakeTicketPosition\":\"\",\"payState\":0,\"cinemaTicketCode\":\"\",\"cinemaAddress\":\"东一条路远东大厦4楼\",\"orderExternalID\":1279776,\"QrCode\":\"\",\"createTime\":\"2019-01-21 17:36:23\",\"cinemaName\":\"牡丹江华夏国际影城\",\"startTime\":\"/Date(1548208200000+0800)/\",\"seatName\":\"8排11座,8排12座\",\"endTime\":\"/Date(1548214680000+0800)/\",\"hallName\":\"2号影厅\",\"movieName\":\"我想吃掉你的胰脏\",\"externalOrderStatus\":10,\"ReturnedOrderStatus\":0}}";
//        JSONObject body = JSONObject.parseObject(json);
//        OrderEntity javaObject = body.getJSONObject("orderdetail").toJavaObject(OrderEntity.class);
//        
//        System.out.println(javaObject.toString());

//        Double lat1 = Double.parseDouble("120.3528795281");
//        Double lon1 = Double.parseDouble("30.3114846742");
//        Double lat2 = Double.parseDouble("30.31");
//        Double lon2 = Double.parseDouble("120.35");
//        System.out.println(getDistance(lon1, lat1, lon2, lat2));
        
        BigDecimal zero = new BigDecimal("0.00");
        System.out.println(zero);
        BigDecimal zero1 = new BigDecimal(0);
        System.out.println(zero1);
        
        System.out.println(zero.compareTo(zero1));
    }

    public static double getDistance(double lon1, double lat1, double lon2, double lat2) {

        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);

        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);

        double c = 2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));

        c = c * 6378.137;// 6378.137赤道半径

        return Math.round(c * 10000d) / 10000d;

    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
}
