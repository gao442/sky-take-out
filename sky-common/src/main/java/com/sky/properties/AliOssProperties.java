package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
//配置属性类：读取配置的信息作为类的属性，然后封装成一个对象
//@ConfigurationProperties(prefix = "sky.alioss"):
// Spring Boot 启动时把 yml 里 sky.alioss.* 下的值
// 自动映射到同名字段,封装成一个 Bean。
@ConfigurationProperties(prefix = "sky.alioss")
@Data
public class AliOssProperties {


    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
/*
配置信息会通过配置属性类的注解@ConfigurationProperties(prefix = "sky.alioss")
自动绑定配置属性类

配置属性类需要向其他类进行注入操作
 */