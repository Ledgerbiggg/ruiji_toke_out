package com.ledger.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.Category;
import com.ledger.reggie.domian.Dish;
import com.ledger.reggie.dto.DishDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ledger
 * @version 1.0
 **/

public interface DishService extends IService<Dish> {

    R<String> saveWithFlavor(DishDto dishDto);

    R<Page<DishDto>> getPage(Long page, Long pageSize ,String name);

    R<Dish> getDishDetail(Long id);

    R<String> edit(DishDto dishDto);

    R<List<DishDto>> getDishList(DishDto dishDto);

    R<String> delete(Long[] ids);

    R<String> changeStatus(Long status,Long[] ids);
}
