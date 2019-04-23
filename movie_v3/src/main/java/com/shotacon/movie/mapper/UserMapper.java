package com.shotacon.movie.mapper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

@Mapper
public interface UserMapper {

    @Select("select * from tihe_common_member where username = #{openid}")
    List<Map<String, Object>> queryCommonMemberByUsername(@Param("openid") String openid);

    @Insert("insert into tihe_common_member (username) SELECT #{openid} FROM DUAL WHERE NOT EXISTS("
            + "SELECT username FROM tihe_common_member WHERE username = #{openid})")
    int insertCommonMemberByUsername(@Param("openid") String openid);

    @Insert("insert into tihe_xcx_user (union_id, token, uid) SELECT #{openid}, #{token}, #{uid} FROM DUAL WHERE NOT EXISTS("
            + "SELECT union_id, token, uid FROM tihe_xcx_user WHERE union_id = #{openid} and token = #{token} and uid = #{uid})")
    int addUser(@Param("openid") String openid, @Param("token") String token, @Param("uid") String uid);

    @Insert("INSERT INTO tihe_xcx_code (phone, code, create_time) VALUES (#{phone}, #{code}, NOW())")
    int saveCode(@Param("phone") String phone, @Param("code") String code);

    @Select("SELECT id FROM tihe_xcx_code WHERE phone = #{phone} AND code = #{code} AND create_time BETWEEN DATE_ADD(NOW(),INTERVAL - 3 MINUTE) AND NOW()")
    int validateCode(@Param("phone") String phone, @Param("code") String code);

    @Select("SELECT * FROM tihe_xcx_user WHERE token = #{token}")
    List<Map<String, Object>> queryUserByToken(@Param("token") String token);

    @Select("SELECT token FROM tihe_xcx_user WHERE union_id = #{openid}")
    List<String> queryUserByOpenId(@Param("openid") String openid);

    @Update("UPDATE tihe_xcx_user SET mobile = #{phone} , is_bind = TRUE WHERE uid = #{uid}")
    int updateUserByPhone(@Param("phone") String phone, @Param("uid") int uid);

    @Update("UPDATE tihe_xcx_user SET check_in = TRUE WHERE uid = #{uid}")
    int checnIn(@Param("uid") int uid);

    @Update("UPDATE tihe_xcx_user SET point = point + #{point} ,point_today = point_today + #{pointToday} WHERE uid = #{uid}")
    int updatePoint(@Param("uid") int uid, @Param("point") int point, @Param("pointToday") int pointToday);

    @Update("INSERT INTO tihe_point_record (uid, point, `desc`) VALUES (#{uid},#{point},#{desc})")
    void addRecord(@Param("uid") int uid, @Param("point") int point, @Param("desc") String desc);

    @UpdateProvider(type = SqlProvider.class, method = "updateUserParam")
    int updateUserByToken(@Param("token") String token, @Param("username") String username,
            @Param("avatarUrl") String avatarUrl, @Param("sex") String sex, @Param("area") String area);

    class SqlProvider {

        public String updateUserParam(@Param("token") String token, @Param("username") String username,
                @Param("avatarUrl") String avatarUrl, @Param("sex") String sex, @Param("area") String area) {
            SQL sql = new SQL().UPDATE("tihe_xcx_user");
            if (!StringUtils.isEmpty(username)) {
                sql.SET(" username = #{username} ");
            }
            if (!StringUtils.isEmpty(avatarUrl)) {
                sql.SET(" avatar_url = #{avatarUrl} ");
            }
            if (!StringUtils.isEmpty(sex)) {
                sql.SET(" sex = #{sex} ");
            }
            if (!StringUtils.isEmpty(area)) {
                sql.SET(" area = #{area} ");
            }
            sql.WHERE(" token = #{token} ");
            return sql.toString();
        }
    }

    @Select("SELECT * FROM tihe_xcx_user WHERE uid = #{uid}")
    List<Map<String, Object>> getUserById(String uid);
}
