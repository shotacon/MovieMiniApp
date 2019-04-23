package com.shotacon.movie.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TicketMapper {

    @Update("UPDATE tihe_movie_ticket_record SET status = #{status} ,need_retry = 1, "
            + "update_time = #{now},lock_seat_apply_key = #{applyKey},express_time = #{lockTime} WHERE order_no = #{orderNo}")
    int updateLockSuccessBack(@Param("lockTime") String expressTime, @Param("applyKey") String orderExternalID,
            @Param("status") String statusLockSuccess, @Param("now") String now, @Param("orderNo") String orderNo);

    @Select("SELECT tt.print_code, tt.verify_code, tt.qr_code,t.* FROM tihe_movie_ticket_record t "
            + " left join tihe_moviefan_order tt on tt.orderexternal_id = t.lock_seat_apply_key "
            + " WHERE t.uid = #{uid} AND t.status IN ('PAY_SUCCESS','TICKETING_DOING','TICKETING_SUCCESS') "
            + " ORDER BY t.schedule_start_time DESC LIMIT #{pageIndex}, #{pageNum}")
    List<Map<String, Object>> getTicket(@Param("uid") int uid, @Param("pageIndex") int pageIndex,
            @Param("pageNum") int pageNum);

    @Select("SELECT * FROM tihe_movie_ticket_record WHERE order_no = #{orderNo}")
    List<Map<String, Object>> getTicketByOrderNo(@Param("orderNo") String orderNo);

    @Select("SELECT * FROM tihe_movie_ticket_record WHERE order_no = #{orderNo} and uid = #{uid}")
    List<Map<String, Object>> getTicketByOrderNoAndUid(@Param("orderNo") String orderNo, @Param("uid") String uid);

    @Select("SELECT * FROM tihe_movie_ticket_record WHERE lock_seat_apply_key = #{orderNo} and uid = #{uid}")
    List<Map<String, Object>> getTicketByAoolyKeyAndUid(@Param("orderNo") String orderExternalID,
            @Param("uid") String uid);

    @Update("update tihe_movie_ticket_record set status = #{status}, update_time = NOW() where lock_seat_apply_key = #{applyKey}")
    int updateTicketStatus(@Param("status") String status, @Param("applyKey") String orderExternalID);

    @Update("update tihe_movie_ticket_record set status = #{status}, update_time = NOW(), ticket_contents = #{qrCode} where lock_seat_apply_key = #{applyKey}")
    int updateTicketStatusAndQrCode(@Param("status") String status, @Param("applyKey") String orderExternalID,
            @Param("qrCode") String qrCode);

    @Update("update tihe_movie_ticket_record set status = #{status}, update_time = NOW() where order_no = #{outTradeNo}")
    int updateTicketStatusByOrderNO(@Param("status") String statusUnlockSuccess,
            @Param("outTradeNo") String outTradeNo);

    @Update("update tihe_movie_ticket_record set status = #{status}, update_time = NOW(),"
            + " pay_amount = #{amount}, coupon_sub = #{c_price}, coupon_code = #{code} where order_no = #{outTradeNo}")
    int updateTicketStatusAndCouponByOrderNO(@Param("status") String statusUnlockSuccess,
            @Param("outTradeNo") String outTradeNo, @Param("code") String code, @Param("amount") String amount,
            @Param("c_price") String c_price);

    @InsertProvider(type = SqlProvider.class, method = "addTicketRecordParam")
    int addTicketRecord(@Param("activity_flag") String activity_flag, @Param("city_code") String city_code,
            @Param("city_name") String city_name, @Param("uid") String uid, @Param("mobile") String mobile,
            @Param("order_no") String order_no, @Param("film_id") String film_id, @Param("film_name") String film_name,
            @Param("cinema_id") String cinema_id, @Param("cinema_name") String cinema_name,
            @Param("schedule_id") String schedule_id, @Param("schedule_date") String schedule_date,
            @Param("schedule_start_time") String schedule_start_time,
            @Param("schedule_end_time") String schedule_end_time, @Param("hall_name") String hall_name,
            @Param("seat_ids") String seat_ids, @Param("seat_names") String seat_names, @Param("status") String status,
            @Param("need_retry") int need_retry, @Param("retry_count") int retry_count,
            @Param("tip_message") String tip_message, @Param("source") String source,
            @Param("recordType") int recordType, @Param("create_time") String create_time,
            @Param("ticketCount") int ticketCount, @Param("batchno") String batchno,
            @Param("origin_price") String origin_price);

    class SqlProvider {

        public String addTicketRecordParam(@Param("activity_flag") String activity_flag,
                @Param("city_code") String city_code, @Param("city_name") String city_name, @Param("uid") String uid,
                @Param("mobile") String mobile, @Param("order_no") String order_no, @Param("film_id") String film_id,
                @Param("film_name") String film_name, @Param("cinema_id") String cinema_id,
                @Param("cinema_name") String cinema_name, @Param("schedule_id") String schedule_id,
                @Param("schedule_date") String schedule_date, @Param("schedule_start_time") String schedule_start_time,
                @Param("schedule_end_time") String schedule_end_time, @Param("hall_name") String hall_name,
                @Param("seat_ids") String seat_ids, @Param("seat_names") String seat_names,
                @Param("status") String status, @Param("need_retry") int need_retry,
                @Param("retry_count") int retry_count, @Param("tip_message") String tip_message,
                @Param("source") String source, @Param("recordType") int recordType,
                @Param("create_time") String create_time, @Param("ticketCount") int ticketCount,
                @Param("batchno") String batchno, @Param("origin_price") String origin_price) {

            String sql = "INSERT INTO tihe_movie_ticket_record (city_code,city_name,uid,"
                    + "mobile,order_no,film_id,film_name,cinema_id,cinema_name,"
                    + "schedule_id,schedule_date,schedule_start_time,schedule_end_time,hall_name,"
                    + "seat_ids,seat_names,status,need_retry,retry_count,tip_message,"
                    + "source,record_type,create_time,ticket_count,batchno,origin_price,"
                    + "activity_flag) VALUES (#{city_code}," + "#{city_name}," + "#{uid}," + "#{mobile},"
                    + "#{order_no}," + "#{film_id}," + "#{film_name}," + "#{cinema_id}," + "#{cinema_name},"
                    + "#{schedule_id}," + "#{schedule_date}," + "#{schedule_start_time}," + "#{schedule_end_time},"
                    + "#{hall_name}," + "#{seat_ids}," + "#{seat_names}," + "#{status}," + "#{need_retry},"
                    + "#{retry_count}," + "#{tip_message}," + "#{source}," + "#{recordType}," + "#{create_time},"
                    + "#{ticketCount}," + "#{batchno}," + "#{origin_price}," + "#{activity_flag})";

            return sql;
        }
    }

    @Select("SELECT * FROM tihe_movie_ticket_record WHERE status = #{status} AND record_type in (1,2)")
    List<Map<String, Object>> getNeedRefundList(@Param("status") String status);

    @Select("SELECT * FROM tihe_movie_ticket_record t where position(#{couponCode} in t.coupon_code) order by t.update_time desc LIMIT 50")
    List<Map<String, Object>> getticketByCouponCode(@Param("couponCode")String couponCode);

}
