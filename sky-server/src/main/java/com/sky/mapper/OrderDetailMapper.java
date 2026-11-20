package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    @Insert("insert into order_detail (name, image, order_id, dish_id, setmeal_id, dish_flavor, number, amount) " +
            "VALUES (#{name}, #{image}, #{orderId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount})")
    void insert(OrderDetail orderDetail);

    void insertBatch(@Param("orderDetailList") List<OrderDetail> orderDetailList);

    @Select("select * from order_detail where order_id=#{orderId}")
    List<OrderDetail> getOrderDetailByOrderId(Long orderId);
}
