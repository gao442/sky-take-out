package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {
//组装Redis模板对象，交给ioc容器，通过bean进行注入
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        //创建Redis模板类对象
        RedisTemplate redisTemplate=new RedisTemplate();
        //设置redis连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置redis key的序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());


        return  redisTemplate;
    }
}
