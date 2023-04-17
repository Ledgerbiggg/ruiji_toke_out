package com.ledger.reggie.controller;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.Employee;
import com.ledger.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;


/**
 * @author ledger
 * @version 1.0
 **/
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployController {

    @Autowired
    private EmployeeService employeeService;

    //员工登录
    @PostMapping("/login")
    public R<Employee> employeeService(@RequestBody Employee employee, HttpServletRequest request) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        qw.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(qw);
        if (emp == null) {
            return R.error("用户不存在");
        }
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        if (emp.getStatus() == 0) {
            return R.error("员工已经被禁用");
        }
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    //退出登录
    @PostMapping("/logout")
    public R<String> loginOut(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出登录成功");
    }

    //员工管理
    @GetMapping("/page")
    public R<Page> getEmployeeList(Long page, Long pageSize, String name) {
        return employeeService.getPage(page, pageSize, name);
    }

    //禁用管理
    @PutMapping
    public R<String> shutDownAccount(HttpServletRequest request,@RequestBody Employee employee) {
        Integer status = employee.getStatus();
        Long employeeId = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateUser(employeeId);
//        employee.setUpdateTime(LocalDateTime.now());
        employee.setStatus(status);
        boolean update = employeeService.updateById(employee);
        return Boolean.TRUE.equals(update) ? R.success("禁用商成功") : R.error("禁用商户失败");
    }


    @PostMapping
    public R<String> saveEmployee(@RequestBody Employee employee, HttpServletRequest request) {
        boolean checkEmployee = employeeService.queryByUserName(employee);
        //如果存在相同的用户
        if (checkEmployee) {
            return R.error("保存失败,用户名已经存在");
        }
//        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employee.setPassword(SecureUtil.md5("123456"));
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateTime(LocalDateTime.now());
        boolean save = employeeService.save(employee);
        return save ? R.success("保存成功") : R.error("保存失败");
    }

    //编辑员工
    @GetMapping("/{id}")
    public R<Employee> editEmployee(@PathVariable("id") Long id){
        //根据id查询员工信息
        Employee employee = employeeService.queryById(id);
        return  R.success(employee);
    }

    //菜品分类







}


