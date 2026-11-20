package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface ReportMapper {
    /**
     * 统计指定时间区间内已完成订单的营业额合计，区间内无订单时返回 null
     * 使用半开区间 [begin, end)，由上层按天传入
     */
    @Select("select sum(amount) from orders " +
            "where status = #{status} and checkout_time >= #{begin} and checkout_time < #{end}")
    Double turnover(@Param("begin") LocalDateTime begin,
                    @Param("end") LocalDateTime end,
                    @Param("status") Integer status);
}
