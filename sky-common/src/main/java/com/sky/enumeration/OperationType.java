package com.sky.enumeration;

/**
 * 数据库操作类型
 */
public enum OperationType {

    /**
     * 更新操作
     */
    UPDATE,

    /**
     * 插入操作
     */
    INSERT
/*
本质是一个只有固定几个实例的类。
UPDATE 和 INSERT 是这个类的两个对象，
全局只有一份


 */
}
