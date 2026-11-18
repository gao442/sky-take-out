package com.sky.controller.admin;

import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {

    public static final String KEY ="SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer shopStatus =(Integer) redisTemplate.opsForValue().get(KEY);
        return Result.success(shopStatus);
    }


}
/*
1. 引入 Redis 依赖
sky-server/pom.xml 里的 spring-boot-starter-data-redis

2. 配置 Redis 地址
application.yml 里的 spring.data.redis

3. 配置 RedisTemplate
RedisConfiguration.java

4. Redis 服务端正在运行
你的电脑上
 */

/*
① 启动时读取 application.yml（以及 application-dev.yml）
        ↓
② 把 spring.data.redis 下的值装进 RedisProperties 对象
        ↓
③ 用 RedisProperties 自动创建 RedisConnectionFactory（连接工厂）
        ↓
④ 把连接工厂传给你写的 redisTemplate(RedisConnectionFactory ...) 方法
        ↓
⑤ 生成的 RedisTemplate 注入到 Controller 的 @Autowired 字段
 */