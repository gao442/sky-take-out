package com.sky.controller.setmealController;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @PostMapping
    public Result InsertSetmeal(@RequestBody SetmealDTO setmealDTO){
        setmealService.InsertSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> PageSetmeal(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setmealService.PageSetmeal(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SetmealVO> getSetmealById(@PathVariable("id") Long id){

        SetmealVO setmealVO = setmealService.getSetmealById(id);
        return Result.success(setmealVO);
    }

    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,@RequestParam Long id){
        setmealService.startOrStop(status,id);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteBatch(@RequestParam List<Long> ids){

        setmealService.deleteBatch(ids);
        return Result.success();
    }

}
