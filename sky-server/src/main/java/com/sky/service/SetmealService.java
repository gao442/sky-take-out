package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SetmealService {
    void InsertSetmeal(SetmealDTO setmealDTO);

    PageResult PageSetmeal(SetmealPageQueryDTO setmealPageQueryDTO);

    void update(SetmealDTO setmealDTO);

    SetmealVO getSetmealById(Long id);

    void startOrStop(Integer status, Long id);

    void deleteBatch(List<Long> ids);

}
