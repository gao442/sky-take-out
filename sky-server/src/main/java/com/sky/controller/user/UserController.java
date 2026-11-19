package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.Category;
import com.sky.entity.Setmeal;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/user/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){

        User user = userService.wxLogin(userLoginDTO);

        //给用户生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        //生成jwt令牌的负载
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        //通过user对象创建返回给前端的对象VO
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return Result.success(userLoginVO);

    }
    /*
    小程序 wx.login() 拿到 code
        ↓
    发给后端 /user/user/login（code 在 UserLoginDTO 里）
            ↓
    后端用 appid + secret + code 请求微信，换到 openid
            ↓
    用 openid 查 user 表：没有就新建用户（自动注册）
            ↓
    生成 JWT 令牌，连同用户 id、openid 返回给小程序
     */

    @GetMapping("/category/list")
    public Result<List<Category>> getCategoryByType(@RequestParam(required = false) Integer type){
        List<Category> categories= userService.getCategoryByType(type);
        return Result.success(categories);
    }

    @GetMapping("/dish/list")
    public Result<List<DishVO>> getDishByCategoryId(Long categoryId){
        //缓存菜品数据
        String key ="dish_"+categoryId;
        ValueOperations valueOperations = redisTemplate.opsForValue();
        List<DishVO> dishes = (List<DishVO>) valueOperations.get(key);
        if(dishes!= null && dishes.size()!= 0){
            return Result.success(dishes);
        }
        dishes = userService.getDishByCategoryId(categoryId);
        valueOperations.set(key,dishes);
        return Result.success(dishes);
    }

    @GetMapping("/setmeal/list")
    public Result<List<Setmeal>> getSetmealByCategoryId(Long categoryId){
        List<Setmeal> setmealList= userService.getSetmealByCategoryId(categoryId);
        return Result.success(setmealList);
    }

    @GetMapping("/setmeal/dish/{id}")
    public Result<List<DishItemVO>> getDishBySetmealId(@PathVariable("id") Long id){
        List<DishItemVO> dishItemVOS = userService.getDishBySetmealId(id);
        return Result.success(dishItemVOS);
    }

}
