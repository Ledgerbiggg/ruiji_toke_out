package com.ledger.reggie.controller;

import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.ShoppingCart;
import com.ledger.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ledger
 * @version 1.0
 **/
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> getList() {
        return shoppingCartService.getList();
    }
    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCard(@RequestBody ShoppingCart shoppingCart){
        return shoppingCartService.addShoppingCard(shoppingCart);
    }
    @PostMapping("/sub")
    public R<ShoppingCart> subShoppingCard(@RequestBody ShoppingCart shoppingCart){
        return shoppingCartService.subShoppingCard(shoppingCart);
    }
    @DeleteMapping("/clean")
    public R<String> clearList(){
        return shoppingCartService.clean();
    }

}
