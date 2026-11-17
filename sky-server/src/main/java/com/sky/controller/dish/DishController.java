package com.sky.controller.dish;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /*
        新增菜品
     */
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        dishService.save(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> pageDish(DishPageQueryDTO dishPageQueryDTO) {
        PageResult result = dishService.pageDish(dishPageQueryDTO);
        return Result.success(result);
    }

    /*
     @RequestParam 能把字符串参数自动解析封装成数组或集合，
     这是 Spring MVC 内置的能力
     */
    /*
    菜品的批量删除
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /*
    数据回显
     */
    @GetMapping("/{id}")
    public Result<DishVO> listDishById(@PathVariable("id") Long id){

        DishVO dishVO = dishService.listDishById(id);
        return Result.success(dishVO);
    }

    /*
    修改菜品
     */
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }


}
