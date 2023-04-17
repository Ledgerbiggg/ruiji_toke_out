package com.ledger.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.Setmeal;
import com.ledger.reggie.domian.SetmealDish;
import com.ledger.reggie.dto.SetmealDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ledger
 * @version 1.0
 **/
public interface SetMealService extends IService<Setmeal> {
    R<Page<SetmealDto>> getPage(Long page, Long pageSize,String name);

    R<String> saveWithDish(SetmealDto setmealDto);

    R<String> deleteSetMeal(Long[] list);

    R<String> changeStatus(Long status, Long[] ids);

    R<List<SetmealDto>> getSetmealDtoList(String categoryId,Integer status);
}
