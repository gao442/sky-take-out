package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id统计菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> pageDish(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id=#{id}")
    Dish getByid(Long id);


    void deleteDishByIds(List<Long> ids);

    @AutoFill(OperationType.UPDATE)
    int update(Dish dish);

    @Select("select * from dish where category_id = #{categoryId} and status = #{status}")
    List<Dish> getDishByCategoryId(@Param("categoryId") Long categoryId, @Param("status") Integer status);

    /**
     * 根据起售/停售状态统计菜品数量
     */
    @Select("select count(id) from dish where status = #{status}")
    Integer countByStatus(Integer status);

}
