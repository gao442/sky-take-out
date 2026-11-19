package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.ShoppingCart;
import com.sky.exception.BaseException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public  class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart =new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //由于每一个请求都是独立的线程，
        //用户端的请求线程里面只会有当前用户的id，因此可以用线程仓库
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //查询当前用户的当前菜品或套餐是否已经在购物车里了
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.getAlready(shoppingCart);

        if(shoppingCartList!=null && shoppingCartList.size()!= 0){
            ShoppingCart shoppingCart1 = shoppingCartList.get(0);
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartMapper.updateNumber(shoppingCart1);
        } else {
            shoppingCart.setCreateTime(LocalDateTime.now());
            //新增的行数量固定为 1，后续重复添加靠 number+1
            shoppingCart.setNumber(1);
            if(shoppingCart.getDishId()!=null){
                Dish dish = dishMapper.getByid(shoppingCart.getDishId());
                if (dish == null) {
                    throw new BaseException(MessageConstant.DISH_NOT_FOUND);
                }
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
            }else if (shoppingCart.getSetmealId()!=null){
                SetmealVO setmeal = setmealMapper.getSetmealById(shoppingCart.getSetmealId());
                if (setmeal == null) {
                    throw new BaseException(MessageConstant.SETMEAL_NOT_FOUND);
                }
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
                //amount 在库中是 NOT NULL，套餐也必须补上单价
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCartMapper.add(shoppingCart);

        }

    }

    @Override
    public List<ShoppingCart> listShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.listShoppingCartByUserId(userId);
        return shoppingCartList;
    }
}
