package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {


    List<ShoppingCart> getAlready(ShoppingCart shoppingCart);


    void updateNumber(ShoppingCart shoppingCart1);


    void add(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id=#{userId}")
    List<ShoppingCart> listShoppingCartByUserId(Long userId);

    @Delete("delete from shopping_cart where user_id=#{userId}")
    void deleteShoppingCart(Long userId);


    Integer getNumber(ShoppingCart shoppingCart);


    void subNumber(ShoppingCart shoppingCart);


    void deleteShoppingCartOne(ShoppingCart shoppingCart);
}
