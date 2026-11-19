package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        //获取insert语句生成的主键值
        Long dishId = dish.getId();
        //像口味表插入数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size()!=0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.saveBatch(flavors);
        }
    }

    @Override
    public PageResult pageDish(DishPageQueryDTO dishPageQueryDTO) {


        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageDish(dishPageQueryDTO);
        PageResult pageResult=new PageResult(page.getTotal(),page.getResult());
        return pageResult;
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //先判断当前菜品是否能删除
        //看看是否起售中或者被套餐关联
        for (Long id : ids) {
            Dish dish = dishMapper.getByid(id);
            Integer status=dish.getStatus();
            if(StatusConstant.ENABLE.equals(status)){
                //起售中
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
//            List<Long> setmealIdList = setmealDishMapper.getSetmealidByDishId(id);
//            if(setmealIdList !=null && setmealIdList.size()>0){
//                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
//            }
        }
        List<Long> setmealIdList= setmealDishMapper.getSetmealidByDishIds(ids);
        if(setmealIdList!=null && setmealIdList.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品数据
        dishMapper.deleteDishByIds(ids);
        //删除菜品关联的口味数据
        dishFlavorMapper.deleteFlavorByDishIds(ids);

    }

    @Override
    public DishVO listDishById(Long id) {

        Dish dish=dishMapper.getByid(id);
        if (dish == null) {
            throw new BaseException(MessageConstant.DISH_NOT_FOUND);
        }
        DishVO dishVO=new DishVO();

        BeanUtils.copyProperties(dish,dishVO);
        List<DishFlavor> dishFlavorList=dishFlavorMapper.listDishFlavorByDishId(id);
        dishVO.setFlavors(dishFlavorList);

        return dishVO;

    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {

        Long id = dishDTO.getId();
        if (id == null) {
            throw new BaseException(MessageConstant.DISH_NOT_FOUND);
        }

        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        int rows = dishMapper.update(dish);
        if (rows == 0) {
            throw new BaseException(MessageConstant.DISH_NOT_FOUND);
        }

        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        List<Long> idList=new ArrayList<>();
        idList.add(dishDTO.getId());

        dishFlavorMapper.deleteFlavorByDishIds(idList);

        if (dishFlavorList != null && !dishFlavorList.isEmpty()) {
            dishFlavorList.forEach(flavor ->
                    flavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.saveBatch(dishFlavorList);
        }
    }

    @Override
    public List<Dish> listDishByCategoryId(Long categoryId) {
        List<Dish> list=setmealDishMapper.listDishByCategoryId(categoryId);
        return list;
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        //只更新 status 字段，其余字段保持 null 由动态 SQL 忽略
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        int rows = dishMapper.update(dish);
        if (rows == 0) {
            throw new BaseException(MessageConstant.DISH_NOT_FOUND);
        }
    }


}
/*
REQUIRED（默认）
有事务就加入，没有就新建。最常用

REQUIRES_NEW
总是新建事务，把当前事务挂起

SUPPORTS
有就加入，没有就以非事务方式执行

NOT_SUPPORTED
总是以非事务方式执行，挂起当前事
 */
/*
同类内部调用：this.save() 自调用不走代理，事务失效 → 需要注入自身或拆到别的 Bean

方法不是 public：Spring 事务基于代理，非 public 方法无效

异常被 catch 吞掉：
方法内部 try-catch 了异常且没抛出，
Spring 感知不到，不会回滚(没有捕捉到事务自己抛出处理事务失效)

数据库引擎不支持：MySQL 的表必须是 InnoDB，MyISAM 不支持事务

@Transactional 加在接口上而非实现类上（Spring 官方推荐加在实现类）
*/
/*
Service 内部把异常接住，情况就不同了
@Transactional
public void updateWithFlavor(DishDTO dishDTO) {
    try {
        dishMapper.update(dish);
        throw new BaseException("修改失败");
    } catch (BaseException e) {
        log.error("发生异常", e);
        // 没有再次抛出
    }
}
不会回滚
调用 Service
    ↓
Service 内部发生异常，但自己处理了
    ↓
Service 正常返回
    ↓
代理没有收到异常 → 通常提交事务
 */