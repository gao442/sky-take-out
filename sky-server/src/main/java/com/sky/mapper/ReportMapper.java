package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

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


    /**
     * 统计截止到指定时刻（不含）的累计注册用户数
     */
    Integer getTotalUserSum(@Param("end") LocalDateTime end);

    /**
     * 统计指定时间区间内新增注册的用户数
     */
    Integer getNewUser(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

    Integer getOrderSj(@Param("begin") LocalDateTime begin,
                       @Param("end") LocalDateTime end,
                       @Param("status") Integer status);


    /**
     * 统计指定时间区间内销量排名前10的商品
     */
    List<GoodsSalesDTO> getSalesTop10(@Param("begin") LocalDateTime begin,
                                      @Param("end") LocalDateTime end,
                                      @Param("status") Integer status);
}
/*
@Param("begin") 括号里的 begin 是给这个 Java 方法参数起的“SQL 引用名”，
既不是必须和形参名一致，
也不是数据库字段名。
它唯一的作用是让你在 XML 里用 #{begin} 取到这个参数的值

MyBatis 不直接读它，不写 @Param 时才靠反射去读
MyBatis读的就是括号里面的字段@Param("begin")

读到什么就应该在#{}里面写什么
 */
