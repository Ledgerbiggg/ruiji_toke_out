package com.ledger.reggie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.reggie.common.BaseContext;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.AddressBook;
import com.ledger.reggie.domian.OrderDetail;
import com.ledger.reggie.domian.Orders;
import com.ledger.reggie.domian.ShoppingCart;
import com.ledger.reggie.dto.OrdersDto;
import com.ledger.reggie.mapper.OrdersMapper;
import com.ledger.reggie.service.*;
import com.ledger.reggie.utils.RandomOrderId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author ledger
 * @version 1.0
 **/
@Service
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Transactional
    @Override
    public R<String> submit(Orders orders) {
        //获取当前用户id
        Long userId = BaseContext.getCurrent();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);
        //获取用户地址
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getId, orders.getAddressBookId());
        AddressBook addressBook = addressBookService.getOne(addressBookLambdaQueryWrapper);
        //获取购物车列表
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        AtomicInteger count = new AtomicInteger(0);
        Long orderNumber = RandomOrderId.nextID(userId);
        //将总金额计算出来，将订单明细搞出
        List<OrderDetail> collect = list.stream().map(shoppingCart -> {
            //每条订单的详细信息的记录
            OrderDetail orderDetail = new OrderDetail();
            //绑定每个订单的id!!!!!
            orderDetail.setOrderId(orderNumber);
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setName(shoppingCart.getName());
            count.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());
            orderDetail.setAmount(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())));
            return orderDetail;
        }).collect(Collectors.toList());
        //订单信息的记录
        orders.setAmount(new BigDecimal(count.get()));
        orders.setStatus(2);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setUserId(userId);
        orders.setPhone(addressBookService.getById(orders.getAddressBookId()).getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setUserName(userService.getById(userId).getName());
        orders.setConsignee(addressBookService.getById(orders.getAddressBookId()).getConsignee());
        orders.setNumber("" + orderNumber);
        save(orders);
        orderDetailService.saveBatch(collect);
        //移出购物车
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        return R.success("下单成功");
    }

    @Override
    public R<Page<OrdersDto>> getOrderList(Long page, Long pageSize) {
        Long userID = BaseContext.getCurrent();
        return getPageR(page, pageSize, 0L, userID, null, null);
    }


    @Override
    public R<Page<OrdersDto>> getOrderPage(Long page, Long pageSize, Long number, LocalDateTime beginTime, LocalDateTime endTime) {
        return getPageR(page, pageSize, number, 0L, beginTime, endTime);
    }


    private R<Page<OrdersDto>> getPageR(Long page, Long pageSize, Long number, Long userId, LocalDateTime beginTime, LocalDateTime endTime) {
        //页面内容
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据时间降序并且跟上用户时间
        lambdaQueryWrapper.orderByDesc(Orders::getCheckoutTime);
        lambdaQueryWrapper.like(number != null, Orders::getNumber, number);
        if (userId != 0) {
            lambdaQueryWrapper.eq(Orders::getUserId, userId);
        }
        if (beginTime != null && endTime != null) {
            lambdaQueryWrapper.between(Orders::getOrderTime, beginTime, endTime);
        }
        //获取当前页
        Page<Orders> page1 = page(ordersPage, lambdaQueryWrapper);
        //OrdersDto的页面
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);
        //赋值属性
        BeanUtil.copyProperties(ordersPage, ordersDtoPage);
        //获取新的OrdersDto页面里面的详细信息
        List<OrdersDto> ordersDtos = page1.getRecords().stream().map(orders -> {
            String ordersId = orders.getNumber();
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            //根据订单id查询订单的详细信息
            lambdaQueryWrapper1.eq(OrderDetail::getOrderId, ordersId);
            List<OrderDetail> list = orderDetailService.list(lambdaQueryWrapper1);
            OrdersDto ordersDto = new OrdersDto();
            BeanUtil.copyProperties(orders, ordersDto);
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());
        //页面变化
        ordersDtoPage.setRecords(ordersDtos);
        return R.success(ordersDtoPage);
    }
}
