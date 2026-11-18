package com.sky.controller.user;


import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
public class ShopController {
    public static final String KEY ="SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer shopStatus =(Integer) redisTemplate.opsForValue().get(KEY);
        return Result.success(shopStatus);
    }


}
