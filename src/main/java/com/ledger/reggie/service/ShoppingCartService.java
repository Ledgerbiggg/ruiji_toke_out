package com.ledger.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.ShoppingCart;

import java.util.List;

/**
 * @author ledger
 * @version 1.0
 **/

public interface ShoppingCartService extends IService<ShoppingCart> {
    R<List<ShoppingCart>> getList();

    R<ShoppingCart> addShoppingCard(ShoppingCart shoppingCart);

    R<ShoppingCart> subShoppingCard(ShoppingCart shoppingCart);

    R<String> clean();
}
