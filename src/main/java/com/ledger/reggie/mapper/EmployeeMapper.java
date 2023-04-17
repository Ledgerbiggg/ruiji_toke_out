package com.ledger.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ledger.reggie.domian.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ledger
 * @version 1.0
 **/
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
