package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;

public interface SetmealService {
    void InsertSetmeal(SetmealDTO setmealDTO);

    PageResult PageSetmeal(SetmealPageQueryDTO setmealPageQueryDTO);

    void update(SetmealDTO setmealDTO);
}
