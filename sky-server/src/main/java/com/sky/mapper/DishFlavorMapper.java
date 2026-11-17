package com.sky.mapper;

import com.sky.dto.DishDTO;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void saveBatch(List<DishFlavor> flavors);


    void deleteFlavorByDishIds(List<Long> ids);


    @Select("select * from dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> listDishFlavorByDishId(Long dishId);


}
