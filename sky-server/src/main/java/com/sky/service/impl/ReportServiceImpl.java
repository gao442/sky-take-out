package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        StringJoiner dateSj=new StringJoiner(",");
        StringJoiner turnoverSj=new StringJoiner(",");
        //逐天统计，保证日期与营业额一一对应；当天没有已完成订单时补 0.0
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateSj.add(date.toString());
            //半开区间 [当天00:00, 次日00:00)，避开 LocalTime.MAX 的纳秒精度问题
            Double turnover = reportMapper.turnover(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), Orders.COMPLETED);
            turnoverSj.add(Double.toString(turnover == null ? 0.0 : turnover));
        }

        return new TurnoverReportVO(dateSj.toString(), turnoverSj.toString());
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {

        StringJoiner dateSj = new StringJoiner(",");
        StringJoiner newUserSj = new StringJoiner(",");
        StringJoiner totalUserSj = new StringJoiner(",");
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateSj.add(date.toString());
            //当天区间 [当天00:00, 次日00:00)
            LocalDateTime dayBegin = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
            //用户总量是累计值：截止到次日 00:00 前注册的所有用户
            Integer totalUser = reportMapper.getTotalUserSum(dayEnd);
            Integer newUser = reportMapper.getNewUser(dayBegin, dayEnd);
            newUserSj.add(Integer.toString(newUser == null ? 0 : newUser));
            totalUserSj.add(Integer.toString(totalUser == null ? 0 : totalUser));
        }
        UserReportVO userReportVO = new UserReportVO(dateSj.toString(),
                totalUserSj.toString(),
                newUserSj.toString());

        return userReportVO;
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        Integer completedStatus = Orders.COMPLETED;
        StringJoiner dateSj = new StringJoiner(",");
        StringJoiner totalOrderSj = new StringJoiner(",");
        StringJoiner completedOrderSj = new StringJoiner(",");
        Integer totalOrderSum = 0;
        Integer completeOrderSum = 0;
        for (LocalDate date = begin;!date.isAfter(end);date=date.plusDays(1)){
            dateSj.add(date.toString());
            Integer totalOrderCount = reportMapper.getOrderSj(date.atStartOfDay(),date.plusDays(1).atStartOfDay(),null);
            Integer completeOrderCount = reportMapper.getOrderSj(date.atStartOfDay(),date.plusDays(1).atStartOfDay(),completedStatus);
            totalOrderSj.add(Integer.toString(totalOrderCount == null ? 0 :totalOrderCount));
            completedOrderSj.add(Integer.toString(completeOrderCount == null ? 0 :completeOrderCount));
            totalOrderSum+=totalOrderCount;
            completeOrderSum+=completeOrderCount;
        }
        double rate = totalOrderSum == 0 ? 0.0 : completeOrderSum.doubleValue() / totalOrderSum;
        return OrderReportVO.builder()
                .dateList(dateSj.toString())
                .orderCountList(totalOrderSj.toString())
                .validOrderCountList(completedOrderSj.toString())
                .totalOrderCount(totalOrderSum)
                .validOrderCount(completeOrderSum)
                .orderCompletionRate(rate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime dayBegin = begin.atStartOfDay();
        LocalDateTime dayEnd = end.plusDays(1).atStartOfDay();

        List<GoodsSalesDTO> salesTop10 = reportMapper.getSalesTop10(dayBegin, dayEnd, Orders.COMPLETED);

        StringJoiner topNameSj = new StringJoiner(",");
        StringJoiner topNumberSj = new StringJoiner(",");
        for (GoodsSalesDTO dto : salesTop10) {
            topNameSj.add(dto.getName());
            topNumberSj.add(String.valueOf(dto.getNumber()==null ? 0 : dto.getNumber()));
        }

        return SalesTop10ReportVO.builder()
                .nameList(topNameSj.toString())
                .numberList(topNumberSj.toString())
                .build();
    }


}
