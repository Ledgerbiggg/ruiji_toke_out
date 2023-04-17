package com.ledger.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.Category;
import com.ledger.reggie.domian.Dish;
import com.ledger.reggie.dto.DishDto;
import com.ledger.reggie.service.DishService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author ledger
 * @version 1.0
 **/
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto) {
        return dishService.saveWithFlavor(dishDto);
    }

    @GetMapping("/page")
    public R<Page<DishDto>> getPage(Long page, Long pageSize, String name) {
        return dishService.getPage(page, pageSize, name);
    }

    @GetMapping("/{id}")
    public R<Dish> getDishDetail(@PathVariable Long id){
        return dishService.getDishDetail(id);
    }

    @PutMapping
    public R<String> editDish(@RequestBody DishDto dishDto){
        return dishService.edit(dishDto);
    }

    @GetMapping("/list")
    public R<List<DishDto>> getDishList(DishDto dishDto){
        return dishService.getDishList(dishDto);
    }
    @DeleteMapping
    public R<String> deleteDish(Long[] ids){
        return dishService.delete(ids);
    }
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable Long status,Long[] ids){
        return dishService.changeStatus(status,ids);
    }





}
