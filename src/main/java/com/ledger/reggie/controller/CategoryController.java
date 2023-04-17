package com.ledger.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.Category;
import com.ledger.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author ledger
 * @version 1.0
 **/
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }
    @GetMapping("/page")
    public R<Page<Category>> getPageList(Long page, Long pageSize){
        return categoryService.getPageList(page,pageSize);
    }
    @DeleteMapping
    public  R<String> delMenu(Long ids){
        return categoryService.delMenu(ids);
    }
    @PutMapping
    public R<String> edit(@RequestBody Category category){
        return categoryService.edit(category);
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> lqw=new LambdaQueryWrapper<>();
        //设置查询type和排序，根据sort的值递增，相同sort的值根据创建时间递减
        lqw.eq(category.getType()!=null,Category::getType,category.getType())
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getCreateTime);
        //生成分类的列表
        List<Category> list = categoryService.list(lqw);
        return R.success(list);
    }







}
