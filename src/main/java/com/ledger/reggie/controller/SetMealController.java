package com.ledger.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ledger.reggie.common.R;
import com.ledger.reggie.dto.SetmealDto;
import com.ledger.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author ledger
 * @version 1.0
 **/
@RestController
@RequestMapping("/setmeal")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    @GetMapping("/page")
    public R<Page<SetmealDto>> getPage(Long page, Long pageSize,String name){
        return setMealService.getPage(page,pageSize,name);
    }

    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){
        return setMealService.saveWithDish(setmealDto);
    }

    @DeleteMapping
    public R<String> deleteSetMeal(Long[] ids){
        return setMealService.deleteSetMeal(ids);
    }

    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable Long status,Long[] ids){
        return setMealService.changeStatus(status,ids);
    }
    @GetMapping("/list")
    public R<List<SetmealDto>> getSetmealDtoList(String categoryId,Integer status){
        return setMealService.getSetmealDtoList(categoryId,status);
    }

}
