package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     *
     * @return
     */
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO ) {
        log.info("新增员工");
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 分页查询
     *
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageEmp(EmployeePageQueryDTO employeePageQueryDTO){

        PageResult pageResult= employeeService.pageEmp(employeePageQueryDTO);
        return Result.success(pageResult);
    }
    /**
     * 员工状态修改
     *
     * @return
     */
    @PostMapping("/status/{status}")
    //按参数名从 URL 查询字符串里取值
    public Result startOrStop(@PathVariable Integer status, @RequestParam Long id){

        employeeService.startOrStop(status,id);

        return Result.success();
    }

    /**
     * 根据id查询员工
     *
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> selectEmpById(@PathVariable Long id){

        Employee employee = employeeService.selectEmpById(id);
        employee.setPassword("*****");
        return Result.success(employee);
    }

    /**
     * 修改员工
     *
     * @return
     */
    @PutMapping()
    public Result updateEmp(@RequestBody EmployeeDTO employeeDTO){

        employeeService.updateEmp(employeeDTO);
        return Result.success();
    }



}
