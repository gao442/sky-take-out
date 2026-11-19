package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
public class DishController {

    //菜品缓存 key 前缀，用户端缓存为 dish_{categoryId}
    private static final String DISH_CACHE_PATTERN = "dish_*";

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /*
        新增菜品
     */
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        dishService.save(dishDTO);
        Long categoryId = dishDTO.getCategoryId();
        String key="dish_"+categoryId;
        cleanCache(key);
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
        cleanCache(DISH_CACHE_PATTERN);
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
        cleanCache(DISH_CACHE_PATTERN);
        return Result.success();
    }

    /*
    菜品起售、停售
     */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable("status") Integer status, @RequestParam("id") Long id){
        dishService.startOrStop(status, id);
        //停售/起售会改变用户端可见的菜品列表，必须清缓存
        cleanCache(DISH_CACHE_PATTERN);
        return Result.success();
    }

    @GetMapping("list")
    public Result<List<Dish>> listDishByCategoryId(@RequestParam Long categoryId){
        List<Dish> dishList = dishService.listDishByCategoryId(categoryId);
        return Result.success(dishList);
    }

    private void cleanCache(String pattern){
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return;
        }
        redisTemplate.delete(keys);
    }

}
