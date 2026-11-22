package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    void insert(Orders order);

    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber
     * @param userId
     * @return
     */
    @Select("select * from orders where number = #{orderNumber} and user_id = #{userId}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     * @return
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 按预期原状态原子更新订单信息（CAS 乐观锁，防止并发状态覆盖）
     * @param orders 更新内容
     * @param expectedStatus 期望的当前订单状态
     * @return 受影响行数
     */
    int updateWithExpectedStatus(@Param("orders") Orders orders,
                                 @Param("expectedStatus") Integer expectedStatus);

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 分页查询历史订单
     * @param currentId
     * @param status
     * @return
     */
    List<OrderVO> getHistoryOrders(@Param("currentId") Long currentId, @Param("status") Integer status);

    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getTimeoutOrder(Integer status, LocalDateTime time);

    /**
     * 管理端条件分页查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据状态统计订单数量
     * @param status
     * @return
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

}

