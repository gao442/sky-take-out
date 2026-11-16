package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/*
用来创建aliOssUtil对象
 手工组装工具类
 */
public class OssConfiguration {
    @Autowired
    private AliOssProperties aliOssProperties;

    //创建oss工具类对象交给spring容器管理
    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil() {

        return new AliOssUtil(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
/*
OssConfiguration 通过外部注入的配置类属性获得在配置类中定义的属性值
然后@Bean 创建 AliOssUtil 对象,填充 得到的4个属性值作为参数


@Bean 必须放在被 Spring 接管
(@Configuration / @Component 系注解)的类里，否则就是摆设。

bean的生命周期：
实例化 →
属性注入Spring 按 Bean 定义找到它依赖谁，从容器里把现成的 Bean 塞进字段 →
Aware 回调 →
BeanPostProcessor 前置 →
初始化(@PostConstruct → afterPropertiesSet →
initMethod)→ BeanPostProcessor 后置(AOP 代理)→
使用 →
销毁(@PreDestroy → destroy →
destroyMethod)
 */