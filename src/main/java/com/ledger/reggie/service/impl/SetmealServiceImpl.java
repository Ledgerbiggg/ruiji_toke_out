package com.ledger.reggie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.reggie.common.CustomException;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.Category;
import com.ledger.reggie.domian.Dish;
import com.ledger.reggie.domian.Setmeal;
import com.ledger.reggie.domian.SetmealDish;
import com.ledger.reggie.dto.SetmealDto;
import com.ledger.reggie.mapper.SetmealMapper;
import com.ledger.reggie.service.CategoryService;
import com.ledger.reggie.service.SetMealService;
import com.ledger.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ledger
 * @version 1.0
 **/
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetMealService {


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 获取菜品的页面信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public R<Page<SetmealDto>> getPage(Long page, Long pageSize, String name) {
        //设置初始页面
        Page<Setmeal> initialPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null, Setmeal::getName, name);
        //查询数据
        Page<Setmeal> page1 = page(initialPage, lqw);
        //获取一个setmeamlDish的page
        Page<SetmealDto> initialPage2 = new Page<>();

        //之前的文件的属性拷贝到新的page上面
        BeanUtil.copyProperties(page1, initialPage2, "records");
        //stream流处理数据，将setmeal转化为setmealdto(里面有name字段可以用来展示)
        List<SetmealDto> collect = page1.getRecords().stream().map(setmeal -> {
            SetmealDto setmealDto = new SetmealDto();
            //赋值属性
            BeanUtil.copyProperties(setmeal, setmealDto);
            //获取categoryId然后根据这个来查询category的名字
            Long categoryId = setmeal.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            //设置categoryId名字
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        //给页面
        initialPage2.setRecords(collect);
        return R.success(initialPage2);
    }

    /**
     * 保存菜品的信息
     *
     * @param setmealDto
     * @return
     */
    @Transactional
    @Override
    public R<String> saveWithDish(SetmealDto setmealDto) {
        Setmeal setmeal = new Setmeal();
        //先保存setmeal套餐
        BeanUtil.copyProperties(setmealDto, setmeal, true);
        save(setmeal);
        //获取setmeal保存之后用雪花算法生成的id序号，
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        //保存菜品
        setmealDishService.saveBatch(setmealDishes);
        stringRedisTemplate.delete("CategoryId:" + setmealDto.getCategoryId());
        return R.success("保存成功");
    }

    @Transactional
    @Override
    public R<String> deleteSetMeal(Long[] ids) {
        //查询count的数量
        LambdaQueryWrapper<Setmeal> lqwForSetMeal = new LambdaQueryWrapper<>();
        lqwForSetMeal.in(Setmeal::getId, ids);
        lqwForSetMeal.eq(Setmeal::getStatus, 1);
        int count = count(lqwForSetMeal);
        //如果里面有起售的，就不能删除
        if (count == 1) {
            throw new CustomException("删除失败,请检查有没有还在起售的商品");
        }
        //批量删除dish和setmeal
        List<Setmeal> setmeals = listByIds(Arrays.asList(ids));
        removeByIds(Arrays.asList(ids));
        LambdaQueryWrapper<SetmealDish> lqwForSetMealDel = new LambdaQueryWrapper<>();
        lqwForSetMealDel.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(lqwForSetMealDel);
        for (Setmeal setmeal : setmeals) {
            Long categoryId = setmeal.getCategoryId();
            stringRedisTemplate.delete("CategoryId:" + categoryId);
        }
        return R.success("删除成功");
    }

    @Override
    public R<String> changeStatus(Long status, Long[] ids) {
        //查询是否是一样的状态
        List<Long> IDS = Arrays.asList(ids);
        LambdaQueryWrapper<Setmeal> lqwForSetMeal = new LambdaQueryWrapper<>();
        lqwForSetMeal.in(Setmeal::getId, IDS);
        lqwForSetMeal.eq(Setmeal::getStatus, status);
        int count = count(lqwForSetMeal);
        if (count > 0) {
            throw new CustomException(status == 0 ? "存在停售商品" : "存在启售的商品");
        }
        //状态一样就更新全部
        update().set("status", status).in("id", IDS).update();
        List<Setmeal> setmeals = listByIds(Arrays.asList(ids));
        for (Setmeal setmeal : setmeals) {
            Long categoryId = setmeal.getCategoryId();
            stringRedisTemplate.delete("CategoryId:" + categoryId);
        }
        return R.success("更改状态成功!!!");
    }

    @Override
    public R<List<SetmealDto>> getSetmealDtoList(String categoryId, Integer status) {
        String listStr = stringRedisTemplate.opsForValue().get("CategoryId:" + categoryId);
        if(StrUtil.isNotEmpty(listStr)){
            return R.success(JSONUtil.toList(listStr,SetmealDto.class));
        }
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Setmeal::getCategoryId, categoryId).eq(Setmeal::getStatus, status);
        List<Setmeal> list = list(lqw);
        List<SetmealDto> collect = list.stream().map(setmeal -> {
            SetmealDto setmealDto1 = new SetmealDto();
            BeanUtil.copyProperties(setmeal, setmealDto1);
            return setmealDto1;
        }).collect(Collectors.toList());
        stringRedisTemplate.opsForValue().set("CategoryId:"+categoryId,JSONUtil.toJsonStr(collect));
        return R.success(collect);
    }


}
