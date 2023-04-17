package com.ledger.reggie.dto;

import com.ledger.reggie.domian.Setmeal;
import com.ledger.reggie.domian.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
