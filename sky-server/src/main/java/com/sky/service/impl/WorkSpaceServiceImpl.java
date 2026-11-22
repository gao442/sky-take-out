package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 根据时间段统计营业数据概览
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        Integer totalOrderCount = reportMapper.getOrderSj(begin, end, null);
        Integer validOrderCount = reportMapper.getOrderSj(begin, end, Orders.COMPLETED);
        Double turnover = reportMapper.turnover(begin, end, Orders.COMPLETED);
        Integer newUsers = reportMapper.getNewUser(begin, end);

        int total = totalOrderCount == null ? 0 : totalOrderCount;
        int valid = validOrderCount == null ? 0 : validOrderCount;
        double turnoverVal = turnover == null ? 0.0 : turnover;
        int newUsersVal = newUsers == null ? 0 : newUsers;

        double orderCompletionRate = total == 0 ? 0.0 : (double) valid / total;
        double unitPrice = valid == 0 ? 0.0 : turnoverVal / valid;

        return BusinessDataVO.builder()
                .turnover(turnoverVal)
                .validOrderCount(valid)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsersVal)
                .build();
    }

    /**
     * 查询今日订单管理总览
     */
    @Override
    public OrderOverViewVO getOrderOverView() {
        LocalDate today = LocalDate.now();
        LocalDateTime begin = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        Integer waitingOrders = reportMapper.getOrderSj(begin, end, Orders.TO_BE_CONFIRMED);
        Integer deliveredOrders = reportMapper.getOrderSj(begin, end, Orders.CONFIRMED);
        Integer completedOrders = reportMapper.getOrderSj(begin, end, Orders.COMPLETED);
        Integer cancelledOrders = reportMapper.getOrderSj(begin, end, Orders.CANCELLED);
        Integer allOrders = reportMapper.getOrderSj(begin, end, null);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders == null ? 0 : waitingOrders)
                .deliveredOrders(deliveredOrders == null ? 0 : deliveredOrders)
                .completedOrders(completedOrders == null ? 0 : completedOrders)
                .cancelledOrders(cancelledOrders == null ? 0 : cancelledOrders)
                .allOrders(allOrders == null ? 0 : allOrders)
                .build();
    }

    /**
     * 查询菜品总览
     */
    @Override
    public DishOverViewVO getDishOverView() {
        Integer sold = dishMapper.countByStatus(StatusConstant.ENABLE);
        Integer discontinued = dishMapper.countByStatus(StatusConstant.DISABLE);

        return DishOverViewVO.builder()
                .sold(sold == null ? 0 : sold)
                .discontinued(discontinued == null ? 0 : discontinued)
                .build();
    }

    /**
     * 查询套餐总览
     */
    @Override
    public SetmealOverViewVO getSetmealOverView() {
        Integer sold = setmealMapper.countByStatus(StatusConstant.ENABLE);
        Integer discontinued = setmealMapper.countByStatus(StatusConstant.DISABLE);

        return SetmealOverViewVO.builder()
                .sold(sold == null ? 0 : sold)
                .discontinued(discontinued == null ? 0 : discontinued)
                .build();
    }
}
