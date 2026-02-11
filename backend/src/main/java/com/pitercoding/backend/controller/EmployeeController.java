package com.pitercoding.backend.controller;

import com.pitercoding.backend.dto.EmployeeItem;
import com.pitercoding.backend.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeItem>> getEmployees() {
        List<EmployeeItem> response = employeeService.getActiveEmployees().stream()
                .map(employee -> {
                    EmployeeItem item = new EmployeeItem();
                    item.setId(employee.getId());
                    item.setName(employee.getName());
                    item.setEmail(employee.getEmail());
                    return item;
                })
                .toList();

        return ResponseEntity.ok(response);
    }
}
