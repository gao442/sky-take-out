package com.sky.controller.category;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result addNewCategory(@RequestBody CategoryDTO categoryDTO){

        categoryService.addNewCategory(categoryDTO);
        return Result.success();
    }

    @PutMapping()
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO){

        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteCategoryById(@RequestParam Long id){

        categoryService.deleteCategoryById(id);
        return Result.success();
    }

    @GetMapping("/page")
    //@RequestParam 是绑定单个简单值的（String/Long/Integer）
    //@RequestParam 只绑单个值，套对象上直接 400
    public Result<PageResult> listPage(CategoryPageQueryDTO categoryPageQueryDTO){

        PageResult pageResult=categoryService.listPage(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/list")
    public Result<List<Category>> listByType(Integer type){

        List<Category> list=categoryService.listByType(type);
        return Result.success(list);
    }

    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,@RequestParam Long id){
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    /*
查询字符串 ?id=10
单个简单值
@RequestParam（可省略）
@GetMapping("/list")

查询字符串 ?page=1&size=10
DTO 对象
不加注解（裸写）
@GetMapping("/page")


路径 /list/10
单个值
@PathVariable
@GetMapping("/list/{id}")


JSON 请求体
DTO 对象
@RequestBody
前端传 JSON
     */
}
