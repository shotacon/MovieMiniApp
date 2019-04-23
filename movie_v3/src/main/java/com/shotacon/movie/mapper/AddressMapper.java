package com.shotacon.movie.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AddressMapper {

    @Insert("INSERT INTO tihe_xcx_user_address (uid, address, phone,name,default_address) "
            + "VALUES (#{uid},#{address},#{phone},#{name}, #{isDefault})")
    int addAddress(@Param("uid") int uid, @Param("address") String address, @Param("phone") String phone,
            @Param("name") String name, @Param("isDefault") boolean isDefault);

    @Select("SELECT * FROM tihe_xcx_user_address WHERE uid = #{uid}")
    List<Map<String, Object>> getAllAddress(@Param("uid") int uid);

    @Select("SELECT * FROM tihe_xcx_user_address WHERE uid = #{uid} AND id = #{id}")
    List<Map<String, Object>> getAddress(@Param("uid") int uid, @Param("id") int id);

    @Update("UPDATE tihe_xcx_user_address SET default_address = TRUE WHERE id = #{id}")
    int setDefault(@Param("id") int id);

    @Update("UPDATE tihe_xcx_user_address SET default_address = FALSE WHERE uid = #{uid}")
    int resetDefault(@Param("uid") int uid);

    @Delete("DELETE FROM tihe_xcx_user_address WHERE id = #{id}")
    int removeAddress(@Param("id") int id);

    @Update("UPDATE tihe_xcx_user_address SET address = #{address} , phone = #{phone},name = #{name} WHERE id = #{id}")
    int updateAddress(@Param("address") String address, @Param("phone") String phone, @Param("name") String name,
            @Param("id") int id);

}
