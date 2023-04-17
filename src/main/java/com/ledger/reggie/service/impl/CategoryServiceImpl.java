package com.ledger.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.reggie.common.CustomException;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.Category;
import com.ledger.reggie.domian.Dish;
import com.ledger.reggie.domian.Setmeal;
import com.ledger.reggie.mapper.CategoryMapper;
import com.ledger.reggie.service.CategoryService;
import com.ledger.reggie.service.DishService;
import com.ledger.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author ledger
 * @version 1.0
 **/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService setmealService;

    @Override
    public R<Page<Category>> getPageList(Long page, Long pageSize) {
        Page<Category> page1 = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据sort字段进行生效排序
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = lambdaQueryWrapper.orderByAsc(Category::getSort);
        //获取页面信息
        Page<Category> page2 = page(page1, categoryLambdaQueryWrapper);
        return R.success(page2);
    }

    @Override
    public R<String> delMenu(Long ids) {
        //查询当前用户是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        int count = dishService.count(dishLambdaQueryWrapper);
        if (count > 0) {
            //如果有关联的菜品就抛出一个业务异常
            throw new CustomException("当前分类下面关联了菜品");
        }
        //查询当前分类是否关联套餐，如果已经关联，抛出一个异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if (count1 > 0) {
            //如果有关联的套餐就抛出一个业务异常
            throw new CustomException("当前分类下面关联了套餐");
        }
        categoryMapper.deleteById(ids);
        //正常删除
        return R.success("删除成功");
    }

    @Override
    public R<String> edit(Category category) {
        boolean save = updateById(category);
        return save ? R.success("修改成功") : R.error("修改失败");
    }
}