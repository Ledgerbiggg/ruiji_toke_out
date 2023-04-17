package com.ledger.reggie.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.Employee;
import com.ledger.reggie.mapper.EmployeeMapper;
import com.ledger.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ledger
 * @version 1.0
 **/
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public R<Page> getPage(Long page, Long pageSize, String name) {
        Page<Employee> iPage =new Page<>(page,pageSize);
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.like(StrUtil.isNotBlank(name),Employee::getName,name);
        lqw.orderByDesc(Employee::getName);
        return R.success(employeeMapper.selectPage(iPage, lqw));
    }

    @Override
    public Employee queryById(Long id) {
        return query().eq("id",id).list().get(0);
    }

    @Override
    public boolean queryByUserName(Employee employee) {
        List<Employee> employees = query().eq("username", employee.getUsername()).list();
        return employees != null && !employees.isEmpty();
    }


}
