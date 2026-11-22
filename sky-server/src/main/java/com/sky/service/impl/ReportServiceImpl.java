package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private WorkSpaceService workSpaceService;

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
/*
row 1	第 2 行	运营数据报表（B2:G2 合并，浅蓝底，宋体 18 号加粗居中）
row 2	第 3 行	时间：（B3:G3 合并，右对齐，cell(1) 写时间范围）
row 3	第 4 行	概览数据（B4:G4 合并，浅黄底，宋体 13 号加粗居中）
row 4	第 5 行	cell(1) 营业额 | cell(2) 货币格 | cell(3) 订单完成率 | cell(4) 百分比格 | cell(5) 新增用户数 | cell(6) 整数格
row 5	第 6 行	cell(1) 有效订单 | cell(2) 整数格 | cell(3) 平均客单价 | cell(4) 货币格 | cell(5~6) 空白带边框
row 6	第 7 行	明细数据（B7:G7 合并，浅黄底，宋体 13 号加粗居中）
row 7	第 8 行	表头：日期 | 营业额 | 有效订单 | 订单完成率 | 平均客单价 | 新增用户数
row 8 ~ 37	第 9~38 行	预留 30 行带细边框、居中对齐及货币/百分比格式的空白明细行，可直接 getRow(8 + i) 填数
 */
    @Override
    public void export(HttpServletResponse httpServletResponse) {
        //查数据(30天)
        BusinessDataVO businessData = workSpaceService.getBusinessData(LocalDate.now().minusDays(30).atStartOfDay(), LocalDate.now().atStartOfDay());
        //写入变革
        //基于模板文件在内存中创建一个excel文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        if (in == null) {
            throw new RuntimeException("运营数据报表模板不存在");
        }
        try (XSSFWorkbook excel = new XSSFWorkbook(in)) {
            XSSFSheet sheet1 = excel.getSheet("Sheet1");

            //时间区间（第3行B列）：30天前 至 昨天
            LocalDate beginDate = LocalDate.now().minusDays(30);
            LocalDate endDate = LocalDate.now().minusDays(1);
            sheet1.getRow(2)
                    .getCell(1)
                    .setCellValue("时间:" + beginDate + "至" + endDate);

            //概览数据（第5、6行）：cell(1)/cell(3) 是标题，值写在 cell(2)/cell(4)/cell(6)
            sheet1.getRow(4).getCell(2).setCellValue(businessData.getTurnover());
            sheet1.getRow(4).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            sheet1.getRow(4).getCell(6).setCellValue(businessData.getNewUsers());
            sheet1.getRow(5).getCell(2).setCellValue(businessData.getValidOrderCount());
            sheet1.getRow(5).getCell(4).setCellValue(businessData.getUnitPrice());

            //明细数据（第9~38行，共30行）：逐天查询，保证每行金额与日期对应
            for (int i = 0; i < 30; i++) {
                LocalDate date = beginDate.plusDays(i);
                BusinessDataVO dailyData = workSpaceService.getBusinessData(
                        date.atStartOfDay(), date.plusDays(1).atStartOfDay());
                Row row = sheet1.getRow(8 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(dailyData.getTurnover());
                row.getCell(3).setCellValue(dailyData.getValidOrderCount());
                row.getCell(4).setCellValue(dailyData.getOrderCompletionRate());
                row.getCell(5).setCellValue(dailyData.getUnitPrice());
                row.getCell(6).setCellValue(dailyData.getNewUsers());
            }

            //通过输出流下载表格
            httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            httpServletResponse.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("运营数据报表.xlsx", StandardCharsets.UTF_8));
            try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
                excel.write(outputStream);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("导出运营数据报表失败", e);
        }
    }

}
