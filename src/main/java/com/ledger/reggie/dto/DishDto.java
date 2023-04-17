package com.ledger.reggie.dto;


import com.ledger.reggie.domian.Dish;
import com.ledger.reggie.domian.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
