package com.sky.controller.dish;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /*
        新增菜品
     */
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        dishService.save(dishDTO);
        return Result.success();
    }
    @GetMapping("/page")
    public Result<PageResult> pageDish(DishPageQueryDTO dishPageQueryDTO){
        PageResult result = dishService.pageDish(dishPageQueryDTO);
        return Result.success(result);
    }

}
