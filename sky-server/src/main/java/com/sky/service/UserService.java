package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.Category;
import com.sky.entity.Setmeal;
import com.sky.entity.User;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;

import java.util.List;

public interface UserService {
    User wxLogin(UserLoginDTO userLoginDTO);

    List<Category> getCategoryByType(Integer type);

    List<DishVO> getDishByCategoryId(Long categoryId);

    List<Setmeal> getSetmealByCategoryId(Long categoryId);

    List<DishItemVO> getDishBySetmealId(Long id);
}
