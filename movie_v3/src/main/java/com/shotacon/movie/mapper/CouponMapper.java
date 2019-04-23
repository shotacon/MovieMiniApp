package com.shotacon.movie.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shotacon.movie.model.CouponCodeSeq;
import com.shotacon.movie.utils.old.CustomSQL;

@Mapper
public interface CouponMapper {

    @Update("UPDATE tihe_movie_coupon_code SET status = 'USED',last_updated_by = #{uid},last_updated_date = NOW(),"
            + "used_date = NOW() WHERE code = #{couponCode}")
    int updateCouponUsed(@Param("couponCode") String couponCode, @Param("uid") String uid);

    @Update("UPDATE tihe_movie_coupon_code SET status = 'NOT_USE',last_updated_by = #{uid},last_updated_date = NOW() WHERE code = #{couponCode}")
    int updateCouponUnUsed(@Param("couponCode") String couponCode, @Param("uid") String uid);

    @Select("SELECT case c.prefix when 'REDBAG_' then 'red packet' when 'EXCHANGE_' then 'exchange' "
            + " else 'CODE_STATUS_NOT_USE' end as type, cc.id AS coupon_code_id,cc.status,cc.code,c.name,c.id AS coupon_id,c.introduction, "
            + " DATE_FORMAT(cc.end_date,'%Y-%m-%d %H:%i:%s') AS end_date,c.maximum_price AS price ,c.range , "
            + " DATE_FORMAT(cc.begin_date,'%Y-%m-%d %H:%i:%s') AS begin_date "
            + " FROM tihe_movie_coupon c LEFT JOIN tihe_movie_coupon_code cc ON c.id = cc.coupon_id"
            + " WHERE cc.member_id = #{uid} AND c.prefix = #{type} AND cc.delete_flag = 0 AND c.delete_flag = 0 "
            + " ORDER BY cc.status asc, cc.creation_date DESC LIMIT #{page},#{num}")
    List<Map<String, Object>> getAllCoupon(@Param("uid") int uid, @Param("page") int page, @Param("num") int num,
            @Param("type") String type);

    @Select("SELECT cc.id AS coupon_code_id,                                              "
            + " cc.code,                                                                  "
            + " c.name,                                                                   "
            + " c.id AS coupon_id,                                                        "
            + " c.introduction,                                                           "
            + " DATE_FORMAT(cc.end_date,'%Y-%m-%d %H:%i:%s') AS end_date,                 "
            + " c.maximum_price AS price,                                                 "
            + " c.range,                                                                  "
            + " DATE_FORMAT(cc.begin_date,'%Y-%m-%d %H:%i:%s') AS begin_date              "
            + " FROM tihe_movie_coupon c                                                  "
            + " LEFT JOIN tihe_movie_coupon_code cc ON c.id = cc.coupon_id                "
            + " WHERE cc.member_id = #{uid}                                               "
            + " AND cc.delete_flag = 0                                                    "
            + " AND c.delete_flag = 0                                                     "
            + " AND c.prefix = #{type}                                                    "
            + " AND cc.status = 'NOT_USE'                                                 "
            + " AND c.minimum_price <= #{totalAmount}                                     "
            + " AND cc.begin_date <= #{now}                                               "
            + " AND cc.end_date >= #{now}                                                 "
            + " AND c.is_enabled = 1                                                      "
            + " AND (c.range IN (                                                         "
            + "      SELECT type FROM tihe_movie_coupon_range                             "
            + "      WHERE (FIND_IN_SET(#{filmId},film_id) OR film_id = 'ALL')            "
            + "      AND (FIND_IN_SET(#{cinemaId},cinema_id) OR cinema_id = 'ALL')        "
            + "      AND (FIND_IN_SET(#{cityCode},city_code) OR city_code = 'ALL'))       "
            + "      OR c.range = 'ALL')                                                  "
            + " ORDER BY cc.creation_date DESC                                            "
            + " LIMIT #{pageIndex},#{pageNum}                                             ")
    List<Map<String, Object>> getUsableRedPacket(@Param("uid") long uid, @Param("totalAmount") double totalAmount,
            @Param("cinemaId") String cinemaId, @Param("filmId") String filmId, @Param("cityCode") String cityCode,
            @Param("pageIndex") int pageIndex, @Param("pageNum") int pageNum, @Param("now") String now,
            @Param("type") String type);

    @Select("SELECT status FROM tihe_movie_coupon_code WHERE code_num = #{cardNo} AND code = #{cardPassword} AND member_id IS NULL")
    List<String> isExist(@Param("cardNo") String cardNo, @Param("cardPassword") String cardPassword);

    @Select("SELECT COUNT(*) FROM tihe_movie_coupon_code as cc INNER JOIN tihe_movie_coupon as mc "
            + "on mc.id = cc.coupon_id where member_id = #{uid} and status = 'NOT_USE' "
            + "and mc.prefix = 'EXCHANGE_' and NOW()<cc.end_date")
    int countCoupon(@Param("uid") int uid);

    @Update("UPDATE tihe_movie_coupon_code SET member_id = #{uid} WHERE code_num = #{cardNo} AND code = #{cardPassword}")
    int bindCoupon(@Param("uid") int uid, @Param("cardNo") String cardNo, @Param("cardPassword") String cardPassword);

    @Update("UPDATE tihe_movie_exchange_card SET member_id = #{uid},status = 'USED',"
            + "use_time = #{now},update_by= #{usedBy} ,update_time= #{now} WHERE card_no = #{cardNo}")
    int updateExchangeCardUsed(@Param("uid") int uid, @Param("cardNo") String cardNo, @Param("now") String now,
            @Param("usedBy") String usedBy);

    @Select("SELECT * FROM tihe_movie_exchange_card WHERE member_id = #{uid} AND status = 'USED' ORDER BY create_time DESC LIMIT #{pageIndex},#{pageNum}")
    List<Map<String, Object>> getUsedExchangeCard(@Param("uid") int uid, @Param("pageIndex") int pageIndex,
            @Param("pageNum") int pageNum);

    @Select("SELECT * FROM tihe_movie_exchange_card WHERE delete_flag = 0 AND card_no = #{cardNo} ORDER BY create_time DESC")
    List<Map<String, Object>> getExchangeCard(@Param("cardNo") String cardNo);

    @Select("SELECT * FROM tihe_movie_coupon WHERE id = #{id}")
    List<Map<String, Object>> getCoupon(@Param("id") long id);

    @Insert("INSERT INTO tihe_coupon_code_seq () values ()")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void genCouponCodeSeq(CouponCodeSeq a);

    @InsertProvider(type = SqlProvider.class, method = "addCouponCodeParam")
    int addCouponCode(@Param("list") List<Map<String, Object>> data);

    class SqlProvider {

        public String addCouponCodeParam(@Param("list") List<Map<String, Object>> data) {
            StringBuilder sb = new StringBuilder(
                    "INSERT INTO tihe_movie_coupon_code (code_num,coupon_id,member_id,code,status,create_by,creation_date,delete_flag,begin_date,end_date) VALUES ");
            for (Map<String, Object> datum : data) {
                CustomSQL sql = new CustomSQL("(?,?,?,?,?,?,?,?,?,?)");
                sql.setString(1, String.valueOf(datum.get("codeNum")));
                sql.setLong(2, Long.valueOf(String.valueOf(datum.get("couponId"))));
                sql.setLong(3, Long.valueOf(String.valueOf(datum.get("memberId"))));
                sql.setString(4, String.valueOf(datum.get("code")));
                sql.setString(5, String.valueOf(datum.get("status")));
                sql.setString(6, String.valueOf(datum.get("createBy")));
                sql.setDateTime(7, new Date());
                sql.setInt(8, Boolean.valueOf(String.valueOf(datum.get("deleteFlag"))) ? 1 : 0);
                sql.setString(9, String.valueOf(datum.get("beginDate")));
                sql.setString(10, String.valueOf(datum.get("endDate")));
                sb.append(sql.getSql()).append(",");
            }

            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }

    }

    @Select("SELECT cc.id AS coupon_code_id,cc.code,c.name,c.id AS coupon_id,c.introduction,DATE_FORMAT(cc.end_date,'%Y-%m-%d %H:%i:%s') AS end_date,"
            + "c.maximum_price AS price ,c.range,DATE_FORMAT(cc.begin_date,'%Y-%m-%d %H:%i:%s') AS begin_date "
            + "FROM tihe_movie_coupon c LEFT JOIN tihe_movie_coupon_code cc ON c.id = cc.coupon_id WHERE cc.code = #{code}")
    List<Map<String, Object>> getCouponByCode(@Param("code") String couponCode);

    @Select("SELECT cc.status AS status, c.maximum_quantity, c.minimum_quantity, c.minimum_price, "
            + " c.maximum_price, c.prefix, c.range "
            + " FROM tihe_movie_coupon c LEFT JOIN tihe_movie_coupon_code cc ON c.id = cc.coupon_id "
            + " WHERE cc.member_id = #{uid} AND cc.delete_flag = 0 AND c.delete_flag = 0 "
            + "AND c.is_enabled = 1 AND cc.begin_date <= #{now} AND cc.end_date >= #{now} AND cc.code = #{code}")
    List<Map<String, Object>> getDiscountRuleMsgByCouponCode(@Param("code") String couponCode, @Param("uid") int uid,
            @Param("now") String now);

    @Select("SELECT * FROM tihe_movie_coupon_range WHERE type = #{range}")
    List<Map<String, Object>> getRangeInfo(@Param("range") String range);

    @Select("SELECT c.price_expression FROM tihe_movie_coupon c "
            + "LEFT JOIN tihe_movie_coupon_code cc ON c.id = cc.coupon_id WHERE cc.member_id = #{uid} AND cc.code = #{code}")
    List<String> getPriceExpression(@Param("code") String code, @Param("uid") int uid);

}
