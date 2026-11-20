package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
//定时任务类，进行自动化任务
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 */1 * * * *")//秒 分 时 日 周 年
    public void processTimeoutOrder(){

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> timeoutOrdersList = orderMapper.getTimeoutOrder(Orders.PENDING_PAYMENT,time);

        if(!timeoutOrdersList.isEmpty() && timeoutOrdersList.size()!= 0){
            for (Orders orders : timeoutOrdersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }


    @Scheduled(cron = "0 0 1 * * ?")//秒 分 时 日 周 年
    public void processDeliverOrder(){
        LocalDateTime time = LocalDateTime.now().plusHours(-1);
        List<Orders> timeoutOrdersList = orderMapper.getTimeoutOrder(Orders.PENDING_PAYMENT,time);
        if(!timeoutOrdersList.isEmpty() && timeoutOrdersList.size()!= 0){
            for (Orders orders : timeoutOrdersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
