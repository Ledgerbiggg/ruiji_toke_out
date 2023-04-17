package com.ledger.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.reggie.common.BaseContext;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.ShoppingCart;
import com.ledger.reggie.mapper.ShoppingCartMapper;
import com.ledger.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ledger
 * @version 1.0
 **/
@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public R<List<ShoppingCart>> getList() {
        Long userID = BaseContext.getCurrent();
        LambdaQueryWrapper<ShoppingCart> lqw=new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,userID);
        lqw.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = list(lqw);
        return R.success(list);
    }
    @Override
    public R<ShoppingCart> addShoppingCard(ShoppingCart shoppingCart) {
        Long userId = BaseContext.getCurrent();
        shoppingCart.setUserId(userId);
        //先查询一些有没有这个菜品
        LambdaQueryWrapper<ShoppingCart> lqw=new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getName,shoppingCart.getName());
        lqw.eq(ShoppingCart::getUserId,userId);
        ShoppingCart one = getOne(lqw);
        LambdaUpdateWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper=new LambdaUpdateWrapper<>();
        //根据是否是套餐还是菜品来指定查询条件
        if(shoppingCart.getDishId()!=null){
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查到就增加数量
        if(one!=null){
            //"update 表 set amount=amount+shoppingCart.getAmount and number=number+1 where dishId=.. "
            //查到了就直接加上价格和数量
            shoppingCart.setNumber(one.getNumber()+1);
            shoppingCartLambdaQueryWrapper.set(ShoppingCart::getNumber,shoppingCart.getNumber());
            update(shoppingCartLambdaQueryWrapper);
            return R.success(shoppingCart);
        }
        //没查到就新增
        shoppingCart.setNumber(1);
        save(shoppingCart);
        return R.success(shoppingCart);
    }
    @Override
    public R<ShoppingCart> subShoppingCard(ShoppingCart shoppingCart) {
        Long userId = BaseContext.getCurrent();
        shoppingCart.setUserId(userId);
        //先查询一些有没有这个菜品
        LambdaQueryWrapper<ShoppingCart> lqw=new LambdaQueryWrapper<>();
        LambdaUpdateWrapper<ShoppingCart> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        if(shoppingCart.getDishId()!=null){
            lambdaUpdateWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            lqw.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            lambdaUpdateWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        lqw.eq(ShoppingCart::getUserId,userId);
        ShoppingCart one = getOne(lqw);
        Integer number = one.getNumber();
        shoppingCart.setNumber(number-1);
        if(number!=1){
            //"update 表 set amount=amount+shoppingCart.getAmount and number=number+1 where dishId=.. "
            //查到了就直接加上价格和数量
            lambdaUpdateWrapper.set(ShoppingCart::getNumber,shoppingCart.getNumber());
            update(lambdaUpdateWrapper);
            return R.success(shoppingCart);
        }
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        remove(lambdaQueryWrapper);
        return R.success(shoppingCart);
    }

    @Override
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> lwq=new LambdaQueryWrapper<>();
        lwq.eq(ShoppingCart::getUserId,BaseContext.getCurrent());
        remove(lwq);
        return R.success("删除成功");
    }
}
