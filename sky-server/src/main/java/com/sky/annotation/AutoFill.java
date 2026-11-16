package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {

    OperationType value();

/*
OperationType value();
代表注解类的属性，是使用注解时给注解赋的值
是枚举类的对象
（属性是一个类的对象，且还没有进行赋值，
定义注解的时候才能对注解的属性进行赋值）

枚举的实例是 JVM 保证全局唯一
所以可以只用==直接对地址进行比较
 */
}
