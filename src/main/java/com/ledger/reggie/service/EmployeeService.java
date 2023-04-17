package com.ledger.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.Employee;

/**
 * @author ledger
 * @version 1.0
 **/
public interface EmployeeService extends IService<Employee> {


    boolean queryByUserName(Employee employee);

    R<Page> getPage(Long page, Long pageSize, String name);


    Employee queryById(Long id);
}
