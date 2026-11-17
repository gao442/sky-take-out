package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    @Select("select setmeal_id from sky_take_out.setmeal_dish where dish_id=#{dishId}")
    List<Long> getSetmealidByDishId(Long dishId);

    List<Long> getSetmealidByDishIds(List<Long> ids);


    void insert(List<SetmealDish> setmealDishList);


    List<Dish> listDishByCategoryId(Long categoryId);


    void deleteAll(Long setmealId);

    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getSetmealDishBySetmealId(Long setmealId);

    void deleteSetmealByids(List<Long> ids);

    int countUnavailableDishesBySetmealId(@Param("setmealId") Long setmealId,
                                          @Param("enabledStatus") Integer enabledStatus);
}
