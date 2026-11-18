package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.*;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.*;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN= "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /*
    微信登录
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //获取openid
        String openid=getOpenId(userLoginDTO.getCode());

        //判断当前的open ID是否为空，如果是空的就登录失败
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断是否为新用户（openid是否在user表里面）
        User user = userMapper.getUserByOpenid(openid);
        if(user == null) {
        //如果是新用户自动完成注册
            //Builder内部回自己创建一个对象
            // ，如果没找到用户直接赋值会报错，找不到这个对象，无法赋值
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();

            userMapper.insertUser(user);
        }

        //返回这个用户对象
        return user;

    }

    private String getOpenId(String code){
        //创建负载发送请求给微信服务接口
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        //调用微信服务接口，并获取当前用户的open ID
        String josn = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(josn);
        String openid = jsonObject.getString("openid");
        return openid;
    }

    @Override
    public List<Category> getCategoryByType(Integer type) {
        //用户端只展示启用的分类，type为空时查询全部类型
        return categoryMapper.listForUser(type, StatusConstant.ENABLE);
    }

    @Override
    public List<DishVO> getDishByCategoryId(Long categoryId) {
        //用户端只展示起售的菜品
        List<Dish> dishes = dishMapper.getDishByCategoryId(categoryId, StatusConstant.ENABLE);
        List<DishVO> dishVOList = new ArrayList<>();
        for (Dish dish : dishes) {
            Long dishId = dish.getId();
            List<DishFlavor> dishFlavorList = dishFlavorMapper.listDishFlavorByDishId(dishId);
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            dishVO.setFlavors(dishFlavorList);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }

    @Override
    public List<Setmeal> getSetmealByCategoryId(Long categoryId) {
        //用户端只展示起售的套餐
        List<Setmeal> setmealList = setmealMapper.getSetmealByCategoryId(categoryId, StatusConstant.ENABLE);
        return setmealList;
    }

    @Override
    public List<DishItemVO> getDishBySetmealId(Long setmealId) {
        //联查setmeal_dish与dish，一条SQL取出套餐内菜品的名称、份数、图片和描述
        return setmealDishMapper.getDishItemBySetmealId(setmealId);
    }
}
