package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
//开启注解方式的事务管理
@Slf4j
@EnableCaching//开启通过注解执行缓存
@EnableScheduling//开启任务调度
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
    }
}
/*
Nginx可以反向代理：提高访问速度
，进行负载的均衡，在集群的服务器中给服务器分配
还能保证后端安全，不会把后端的地址暴露在互联网上
 */
/*
@EnableCaching
总开关，没有它下面全部失效
加在启动类/配置类上
一次性的

@Cacheable
查缓存，命中就直接返回，没命中才执行方法并把返回值写入缓存
方法执行前先查,先查询在把数据放入redis
查询类方法

@CachePut
永远执行方法，然后把返回值写入/覆盖缓存
方法执行后，不进行查询，直接执行方法，方法执行后直接写入缓存（覆盖写入）
更新后用新值刷新缓存

@CacheEvict
删除缓存
默认方法执行后删
新增/修改/删除后清缓存（方法执行后删除缓存）

@Caching
组合上面多个注解
—
一次操作要同时删多个 cache

@CacheConfig
类级别抽公共配置（cacheNames、keyGenerator）
类上
同类方法缓存名一致时少写重复

 */
/*
cacheNames（缓存区名字） + key（缓存 key，SpEL 表达式） 最终拼成 Redis Key：
默认 dish::1（cacheName::key）。


condition vs unless ：
前者在进缓存前判断（决定是否走缓存），
后者在拿到结果后判断（决定是否写缓存）。
判断参数用 condition，判断返回值用 unless。


key 常用 SpEL：
参数名 #categoryId、对象属性 #dishDTO.categoryId、返回值 #result.id、下标 #p0。
不写 key 时默认用所有参数当 key。

@CacheEvict 特有：allEntries = true（清空整个 cacheNames）
、beforeInvocation = true（方法执行前删，方法抛异常也照样删）
 */