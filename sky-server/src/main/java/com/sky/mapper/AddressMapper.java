package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AddressMapper {
    @Insert("insert into address_book (user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default) " +
            "values (#{userId}, #{consignee}, #{sex}, #{phone}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void addAddress(AddressBook addressBook);


    @Select("select * from address_book where user_id=#{userId}")
    List<AddressBook> getAddress(Long userId);

    /**
     * 修改地址：只更新非空字段，where 带 user_id 防止改到别人的地址
     */

    void updateAddress(AddressBook addressBook);


    /**
     * 根据 id 查询地址，带 userId 防止查到别人的地址
     */
    @Select("select * from address_book where id = #{id} and user_id = #{userId}")
    AddressBook getAddressById(@Param("id") Long id, @Param("userId") Long userId);


    @Delete("delete from address_book where id = #{id} and user_id = #{userId}")
    void deleteAddressById(@Param("id") Long id, @Param("userId") Long userId);


    /**
     * 把该用户的所有地址默认标记置为 0
     */
    @Update("update address_book set is_default = 0 where user_id = #{userId}")
    void clearDefault(Long userId);


    /**
     * 把指定地址设为默认地址
     */
    @Update("update address_book set is_default = 1 where id = #{id} and user_id = #{userId}")
    void setDefault(@Param("id") Long id, @Param("userId") Long userId);
}
