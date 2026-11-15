package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.json.JacksonObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login");
    }

    /**
     * 接口文档信息，Knife4j（底层为 springdoc-openapi）会基于该配置生成文档
     * 访问地址：http://localhost:8080/doc.html
     *
     * @return
     */
    @Bean
    public OpenAPI skyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("苍穹外卖项目接口文档")
                        .version("2.0")
                        .description("苍穹外卖项目接口文档"));
    }

    /**
     * Knife4j 静态资源映射
     * doc.html 与 webjars 下的 js/css 都打包在 knife4j-openapi3-ui 的 META-INF/resources 中
     * springdoc 的自动配置只注册 /swagger-ui/**，不会注册 /webjars/**，所以必须在这里显式映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始设置接口文档静态资源映射...");
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 扩展消息转换器，统一日期时间格式
     *
     * @param converters
     */
    @Override
    //扩展springmvc的消息转换器，对日期进行和石化
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建一个消息转化器
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //需要为消息转化器设置一个对象转化器
        //将Java对象序列化为json数据
        converter.setObjectMapper(new JacksonObjectMapper());
        //将消息转化器加到容器里面
        //converters就是整个mvc的转化器
        converters.add(0, converter);
    }
/*
第 1 步：JacksonObjectMapper 定义了格式规则
sky-common 里的 JacksonObjectMapper 为 LocalDateTime 全局注册了序列化器：

第 2 步：WebMvcConfiguration 把它注册为全局转换器
 */


}