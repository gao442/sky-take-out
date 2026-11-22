package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

/**
 * 工作台数据统计服务
 */
public interface WorkSpaceService {

    /**
     * 根据时间段统计营业数据概览
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询今日订单管理总览
     */
    OrderOverViewVO getOrderOverView();

    /**
     * 查询菜品总览
     */
    DishOverViewVO getDishOverView();

    /**
     * 查询套餐总览
     */
    SetmealOverViewVO getSetmealOverView();
}
