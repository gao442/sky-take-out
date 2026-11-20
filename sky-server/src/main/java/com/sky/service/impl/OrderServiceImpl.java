package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private WebSocketServer webSocketServer;


    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //判断是否购物车为空地址为空
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        AddressBook addressBook = addressMapper.getAddressById(addressBookId, BaseContext.getCurrentId());
        if(addressBook==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //mybatis拿出来的集合一定不是null的
        //必须对其进行空判断
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.listShoppingCartByUserId(BaseContext.getCurrentId());
        if(shoppingCartList.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //向订单插入一条数据
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,order);
        order.setOrderTime(LocalDateTime.now());
        order.setPayStatus(Orders.UN_PAID);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setPhone(addressBook.getPhone());
        order.setConsignee(addressBook.getConsignee());
        order.setUserId(BaseContext.getCurrentId());
        order.setAddress(addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDetail());

        orderMapper.insert(order);

        Long orderId = order.getId();
        List<OrderDetail> orderDetailList=new ArrayList<>();
        //向订单明细表插入数据
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);
        //清空购物车
        shoppingCartMapper.deleteShoppingCart(BaseContext.getCurrentId());

        //封装返回结果
        return OrderSubmitVO.builder()
                .id(orderId)
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();

    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        // 查询当前订单
        Orders ordersDB = orderMapper.getByNumberAndUserId(ordersPaymentDTO.getOrderNumber(), userId);
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        log.info("模拟支付：订单号 {} 支付成功", ordersPaymentDTO.getOrderNumber());

        // 无商户号情况下的模拟支付：直接更新订单状态为已支付、待接单
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .payMethod(ordersPaymentDTO.getPayMethod() != null ? ordersPaymentDTO.getPayMethod() : 1)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        // 通过websocket向客户端浏览器推送消息 type orderId content
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1); // 1表示来单提醒 2表示客户催单
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号：" + ordersPaymentDTO.getOrderNumber());

        webSocketServer.sendToAllClient(JSON.toJSONString(map));

        // 返回空的OrderPaymentVO，前端判断后直接跳转成功页
        return new OrderPaymentVO();
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    @Override
    public void paySuccess(String outTradeNo) {
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);
        if (ordersDB != null) {
            // 根据订单id更新订单的状态、支付状态、结账时间
            Orders orders = Orders.builder()
                    .id(ordersDB.getId())
                    .status(Orders.TO_BE_CONFIRMED)
                    .payStatus(Orders.PAID)
                    .checkoutTime(LocalDateTime.now())
                    .build();

            orderMapper.update(orders);

            // 通过websocket向客户端浏览器推送消息 type orderId content
            Map<String, Object> map = new HashMap<>();
            map.put("type", 1); // 1表示来单提醒 2表示客户催单
            map.put("orderId", ordersDB.getId());
            map.put("content", "订单号：" + outTradeNo);

            webSocketServer.sendToAllClient(JSON.toJSONString(map));
        }
    }

    @Override
    public PageResult getHistoryOrders(Integer page, Integer pageSize, Integer status) {

        PageHelper.startPage(page, pageSize);
        Page<OrderVO> orderVOList = (Page<OrderVO>) orderMapper.getHistoryOrders(BaseContext.getCurrentId(), status);
        for (OrderVO orderVO : orderVOList) {
            Long orderId = orderVO.getId();
            List<OrderDetail> orderDetailList = orderDetailMapper.getOrderDetailByOrderId(orderId);
            orderVO.setOrderDetailList(orderDetailList);
        }

        PageResult pageResult =new PageResult(orderVOList.getTotal(),orderVOList.getResult());

        return pageResult;
    }

    @Override
    public OrderVO getOrderDetailById(Long id) {
        Orders orders = orderMapper.getById(id);
        OrderVO orderVO =new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        List<OrderDetail> orderDetailList = orderDetailMapper.getOrderDetailByOrderId(id);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {

        Orders order = orderMapper.getById(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!order.getUserId().equals(BaseContext.getCurrentId())) {
            throw new OrderBusinessException(MessageConstant.USER_NOT_LOGIN);
        }
        Integer status = order.getStatus();
        if (Orders.PENDING_PAYMENT.equals(status)) {
            //待付款：直接取消
            orderMapper.update(Orders.builder()
                    .id(id)
                    .status(Orders.CANCELLED)
                    .cancelReason("用户取消")
                    .cancelTime(LocalDateTime.now())
                    .build());
        } else if (Orders.TO_BE_CONFIRMED.equals(status)) {
            //已支付待接单：模拟退款后再取消
            orderMapper.update(Orders.builder()
                    .id(id)
                    .status(Orders.CANCELLED)
                    .payStatus(Orders.REFUND)
                    .cancelReason("用户取消")
                    .cancelTime(LocalDateTime.now())
                    .build());
        } else {
            //已接单/派送中/已完成等状态不可取消
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

    }

    @Override
    @Transactional
    public void repetitionOrder(Long id) {
        Orders order = orderMapper.getById(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!order.getUserId().equals(BaseContext.getCurrentId())) {
            throw new OrderBusinessException(MessageConstant.USER_NOT_LOGIN);
        }

        List<OrderDetail> orderDetailList = orderDetailMapper.getOrderDetailByOrderId(id);
        for (OrderDetail orderDetail : orderDetailList) {
            ShoppingCart shoppingCart = ShoppingCart.builder()
                    .name(orderDetail.getName())
                    .image(orderDetail.getImage())
                    .amount(orderDetail.getAmount())
                    .createTime(LocalDateTime.now())
                    .userId(BaseContext.getCurrentId())
                    .number(orderDetail.getNumber())
                    .build();
            if (orderDetail.getDishId() == null){
                shoppingCart.setSetmealId(orderDetail.getSetmealId());
            }else{
                shoppingCart.setDishId(orderDetail.getDishId());
                if(orderDetail.getDishFlavor()!=null){
                    shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
                }
            }
            shoppingCartMapper.add(shoppingCart);
        }

    }

    /**
     * 客户催单
     *
     * @param id
     */
    @Override
    public void reminder(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("type", 2); // 1表示来单提醒 2表示客户催单
        map.put("orderId", id);
        map.put("content", "订单号：" + ordersDB.getNumber());

        // 通过websocket向客户端浏览器推送消息
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

}
