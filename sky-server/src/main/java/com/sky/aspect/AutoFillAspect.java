package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class AutoFillAspect {
    //定义切入点
    @Pointcut("@annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){};

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        //先通过契入点得到方法签名
        /*
        joinPoint 是切面拦截到的"案发现场快照"。报警记录
        每次有 Mapper 方法被调用，Spring AOP
        就把这次调用的全部信息打包成一个 JoinPoint 对象传给 autoFill 方法
        包括：被调的是哪个方法、传了什么参数、目标对象是谁。

        getSignature() 是从快照里拿出"签名"Signature  嫌疑人身份栏
        即被拦截方法的身份信息——方法名、参数类型、返回类型等
        得到的东西不是真正的方法，只是方法信息

        getMethod() 负责把描述信息还原成真正的方法对象
         */
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //再通过方法签名得到注解对象
        //并指定这个注解的字节码文件AutoFill.class
        //强转成method签名才能调用方法
        AutoFill autoFill= methodSignature.getMethod().getAnnotation(AutoFill.class);
        //通过注解对象得到提前设置好的注解属性值
        OperationType operationType = autoFill.value();

        //通过切入点获取全部实参数组 （约定对象是第一个）
        Object[] args = joinPoint.getArgs();
        Object entity=args[0];

        LocalDateTime now=LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        if(operationType == OperationType.INSERT){
            //为四个公共字段赋值
            try {
                //Class<?> c2 = entity.getClass();
                // 运行时才知道类型
                //通过object.getclass.getDeclaredMethod(方法名字，  参数类型的类)可以拿到方法
                //再可以通过invoke调用方法
                //entity.getClass()是为了拿到 entity 这个对象所属类的"说明书
                //Object里面没有任何方法， getClass() 让 JVM 在运行时查看它的真实类型
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

                //通过反射调用set方法，为公共字段赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {

                e.printStackTrace();
            }

        } else if (operationType==OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

                //通过反射调用set方法，为公共字段赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }


    }

}
