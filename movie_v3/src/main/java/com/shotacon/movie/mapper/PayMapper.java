package com.shotacon.movie.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shotacon.movie.model.WXOrderDB;

@Mapper
public interface PayMapper {

    @Update("update tihe_movie_ticket_record set status = #{status}, pay_amount = #{pay_amount}, "
            + " update_time = #{updateTime} where order_no = #{order_no}")
    int updateTicketRecordSucc(@Param("status") String payStatus, @Param("updateTime") String successDatetime,
            @Param("order_no") String order_no, @Param("pay_amount") String total_fee);

    @Update("update tihe_movie_ticket_record set status = #{status}, "
            + " update_time = #{updateTime} where order_no = #{order_no}")
    int updateTicketRecordFail(@Param("status") String payStatus, @Param("updateTime") String successDatetime,
            @Param("order_no") String order_no);

    @Insert("INSERT INTO tihe_wx_order (order_no, amount, body, status, openId, update_time, create_time)"
            + " VALUES(#{p.orderNo}, #{p.amount}, #{p.body}, #{p.status}, #{p.openId}, now(), now())"
            + " ON DUPLICATE KEY update update_time = now()")
    int saveWXOrderDB(@Param("p") WXOrderDB order);

    @Update("UPDATE tihe_wx_order SET status = #{status} WHERE order_no = #{orderNo}")
    int updateWXOrderDB(@Param("orderNo") String orderNo, @Param("status") String status);

    @Update("UPDATE tihe_wx_order SET status = #{status}, message = #{msg} WHERE order_no = #{orderNo}")
    int updateWXOrderDBFail(@Param("orderNo") String orderNo, @Param("status") String status,
            @Param("msg") String message);

    @Select("select * from tihe_wx_order WHERE order_no = #{orderNo}")
    List<Map<String, Object>> queryWXOrderDB(@Param("orderNo") String orderNo);

    @Select("select * from tihe_wx_order where status = #{status} and update_time >= CURRENT_TIMESTAMP - INTERVAL 30 MINUTE")
    List<Map<String, Object>> queryPayingRecord(@Param("status") String status);

    @Select("select * from tihe_movie_ticket_record where status = #{status} and create_time >= CURRENT_TIMESTAMP - INTERVAL 30 MINUTE")
    List<Map<String, Object>> queryTicketByStatus(String statusTicketingDoing);
}
