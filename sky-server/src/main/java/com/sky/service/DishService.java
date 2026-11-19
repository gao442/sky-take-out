package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void save(DishDTO dishDTO);

    PageResult pageDish(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(List<Long> ids);

    DishVO listDishById(Long id);

    void updateWithFlavor(DishDTO dishDTO);

    List<Dish> listDishByCategoryId(Long categoryId);

    /**
     * 菜品起售、停售
     * @param status 0 停售 1 起售
     * @param id 菜品id
     */
    void startOrStop(Integer status, Long id);
}
