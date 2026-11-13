package com.sky.config;

import com.github.pagehelper.PageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * MyBatis 配置类
 * Spring Boot 3 不再加载 spring.factories 中的自动配置，
 * pagehelper-spring-boot-starter 因此失效，这里手动注册分页插件
 */
@Configuration
public class MybatisConfiguration {

    /**
     * 注册 PageHelper 分页拦截器，由 mybatis-spring-boot-starter 自动装配到 SqlSessionFactory
     *
     * @return
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        // 指定数据库方言
        properties.setProperty("helperDialect", "mysql");
        // 页码小于 1 时查第一页，超过最大页时查最后一页
        properties.setProperty("reasonable", "true");
        interceptor.setProperties(properties);
        return interceptor;
    }
}
