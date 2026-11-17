package com.sky.controller.setmealController;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

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

}
