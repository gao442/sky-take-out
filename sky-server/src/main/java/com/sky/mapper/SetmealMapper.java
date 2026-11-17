package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.poi.sl.draw.geom.GuideIf;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id统计套餐数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    Page<SetmealVO> getSetmealPage(SetmealPageQueryDTO setmealPageQueryDTO);

    @AutoFill(OperationType.INSERT)
    void update(Setmeal setmeal);

    SetmealVO getSetmealById(Long id);

    @AutoFill(OperationType.UPDATE)
    void startOrStop(Setmeal setmeal);

    void deleteBatch(List<Long> ids);

    @Select("select status from setmeal where id=#{id}")
    Integer getStatusByid(Long id);


}
