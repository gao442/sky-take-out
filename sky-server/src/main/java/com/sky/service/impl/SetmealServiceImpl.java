package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public  class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void InsertSetmeal(SetmealDTO setmealDTO) {
        List<SetmealDish> setmealDishList=setmealDTO.getSetmealDishes();
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);
        Long setmealId=setmeal.getId();
        setmealDishList.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.insert(setmealDishList);

    }

    @Override
    public PageResult PageSetmeal(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealPageQueryDTO,setmeal);
        Page<SetmealVO> page = setmealMapper.getSetmealPage(setmealPageQueryDTO);

        PageResult pageResult=new PageResult(page.getTotal(),page.getResult());
        return pageResult;
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        setmealDishList.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.deleteAll(setmealId);
        setmealDishMapper.insert(setmealDishList);

    }

    @Override
    @Transactional
    public SetmealVO getSetmealById(Long id) {
        SetmealVO setmealVO = setmealMapper.getSetmealById(id);
        if (setmealVO == null) {
            return null;
        }
        List<SetmealDish> setmealDishList=setmealDishMapper.getSetmealDishBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishList);
        return setmealVO;

    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        // 仅起售套餐时校验关联菜品，停售不受菜品状态限制
        if (StatusConstant.ENABLE.equals(status)) {
            List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDishBySetmealId(id);
            for (SetmealDish setmealDish : setmealDishes) {
                Integer dishStatus = setmealDishMapper.getSetmealDishStatus(setmealDish);
                if (!StatusConstant.ENABLE.equals(dishStatus)) {
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }

        setmealMapper.startOrStop(setmeal);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {

        for (Long id : ids) {
            Integer status = setmealMapper.getStatusByid(id);
            if(status == 1){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        setmealDishMapper.deleteSetmealByids(ids);
        setmealMapper.deleteBatch(ids);

    }

}
