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
import com.ledger.reggie.domian.DishFlavor;
import com.ledger.reggie.dto.DishDto;
import com.ledger.reggie.mapper.DishMapper;
import com.ledger.reggie.service.CategoryService;
import com.ledger.reggie.service.DishFlavorService;
import com.ledger.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;

/**
 * @author ledger
 * @version 1.0
 **/
@Service
@Slf4j
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Transactional
    @Override
    public R<String> saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息
        save(dishDto);
        //保存菜品口口味表
        List<DishFlavor> flavors = dishDto.getFlavors();
        Long id = dishDto.getId();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }
        dishFlavorService.saveBatch(flavors);
        stringRedisTemplate.delete("CategoryId:" + dishDto.getCategoryId());
        return R.success("添加成功");
    }

    @Override
    public R<Page<DishDto>> getPage(Long page, Long pageSize, String name) {
        Page<Dish> page1 = new Page<>(page, pageSize);
        //根据所需要的名称来查询需要的菜品
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        //模糊查询
        lqw.like(name != null, Dish::getName, name);
        //查询dish
        Page<Dish> page2 = page(page1, lqw);
        //创建一个dishDTO的页面
        Page<DishDto> pages = new Page<>(page, pageSize);
        //将页面的相关属性都拷贝到dishDTO的页面里面
        copyProperties(page2, pages, "records");
        //根据dish里面的categoryId去查找对应的菜品名称
        List<DishDto> collect = page2.getRecords().stream().map(dish -> {
            //将dish的相关属性都拷贝到dishDTO上面
            DishDto dishDto = copyProperties(dish, DishDto.class);
            //搜索菜品种类名
            Category category = categoryService.getById(dish.getCategoryId());
            //设置种类名称
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());
        //将页面的记录直接设置到上面去
        pages.setRecords(collect);
        return R.success(pages);
    }

    @Override
    public R<Dish> getDishDetail(Long id) {
        //根据菜品的id查询
        DishDto dishDto = new DishDto();
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //根据菜品的id查询口味
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Dish one = getById(id);
        lambdaQueryWrapper.eq(Category::getId, one.getCategoryId());
        //根据菜品的id查询菜品种类
        Category category = categoryService.getOne(lambdaQueryWrapper);
        //复制菜品dish
        copyProperties(one, dishDto);
        //添加口味
        dishDto.setFlavors(list);
        //获取菜品种类
        dishDto.setCategoryName(category.getName());
        return R.success(dishDto);
    }

    /**
     * 编辑菜品(需要直接删除口味再进行添加，不然调整口味可能会失败)
     *
     * @param dishDto
     * @return
     */
    @Override
    public R<String> edit(DishDto dishDto) {
        //将dishDto分成两个类保存到数据里面
        //获取味道列表
        Dish dish = new Dish();
        //获取dish信息
        BeanUtil.copyProperties(dishDto, dish, true);
        //更新dish和味道
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, dishDto.getId());
        //我直接移出口味和菜品直接添加草他妈的
        dishFlavorService.remove(lqw);
        this.removeById(dishDto.getId());
        //直接调用保存方法，因为dishDto里面有id，所以所有的id都不会变
        saveWithFlavor(dishDto);
        stringRedisTemplate.delete("CategoryId:" + dishDto.getCategoryId());
        return R.success("保存成功");
    }

    @Override
    public R<List<DishDto>> getDishList(DishDto dishDto) {
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        //先走一趟redis
        String res = stringRedisTemplate.opsForValue().get("CategoryId:" + dishDto.getCategoryId());
        if (StrUtil.isNotBlank(res)) {
            List<DishDto> dishDtoList = JSONUtil.toList(res, DishDto.class);
            return R.success(dishDtoList);
        }
        //根据传过来的分类id来查找需要的菜品种类
        lqw.eq(dishDto.getCategoryId() != null, Dish::getCategoryId, dishDto.getCategoryId());
        //根据sort值升序排
        lqw.orderByAsc(Dish::getSort);
        //根据创建时间降序牌
        lqw.orderByDesc(Dish::getCreateTime);
        //获取信息
        List<Dish> list = list(lqw);
        //将dis转化成dishDto，增加一个字段是flavor属性
        List<DishDto> collect = list.stream().map(dish -> {
            DishDto dishDto1 = new DishDto();
            copyProperties(dish, dishDto1);
            Long dishId = dish.getId();
            LambdaQueryWrapper<DishFlavor> lqwForDishFlavor = new LambdaQueryWrapper<>();
            lqwForDishFlavor.in(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(lqwForDishFlavor);
            dishDto1.setFlavors(dishFlavors);
            return dishDto1;
        }).collect(Collectors.toList());
        stringRedisTemplate.opsForValue().set("CategoryId:" + dishDto.getCategoryId(), JSONUtil.toJsonStr(collect));
        return R.success(collect);
    }

    /**
     * 删除菜品或者批量删除菜品
     *
     * @param ids
     * @return
     */
    @Override
    public R<String> delete(Long[] ids) {
        List<Long> IDS = Arrays.asList(ids);
        //查询是否是停售的商品
        LambdaQueryWrapper<Dish> lqwForDish = new LambdaQueryWrapper<>();
        lqwForDish.in(Dish::getId, IDS);
        lqwForDish.eq(Dish::getStatus, 1);
        int count = count(lqwForDish);
        if (count > 0) {
            throw new CustomException("存在没有停售的商品");
        }
        //移出菜品之前查找到数据，然后根据种类id去查询
        List<Dish> dishes = listByIds(Arrays.asList(ids));
        //批量删除ids的数据
        removeByIds(IDS);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId, IDS);
        dishFlavorService.remove(lambdaQueryWrapper);
        for (Dish dish : dishes) {
            Long categoryId = dish.getCategoryId();
            stringRedisTemplate.delete("CategoryId:" + categoryId);
        }
        return R.success("删除成功");
    }

    @Override
    public R<String> changeStatus(Long status, Long[] ids) {
        //先检查是不是都为同一个状态
        List<Long> IDS = Arrays.asList(ids);
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.in(Dish::getId, IDS);
        //判断状态的数量
        lqw.eq(Dish::getStatus, status);
        int count = count(lqw);
        if (count > 0) {
            throw new CustomException(status == 0 ? "存在停售菜品" : "存在启售菜品");
        }
        //批量删修改dish和Flavor
        LambdaQueryWrapper<Dish> lqwForDish = new LambdaQueryWrapper<>();
        lqwForDish.eq(Dish::getStatus, status);
        update().set("status", status).in("id", IDS).update();
        List<Dish> dishes = listByIds(Arrays.asList(ids));
        for (Dish dish : dishes) {
            Long categoryId = dish.getCategoryId();
            stringRedisTemplate.delete("CategoryId:" + categoryId);
        }
        return R.success(status == 0 ? "停售成功" : "起售唱歌");
    }
}


























