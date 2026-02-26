package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserResponseDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.Expense;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private UserService service;
    @Autowired
	public UserController(UserService service) {
		super();
		this.service = service;
	}
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id)
    {
    	User user=service.getUserById(id);
    	UserResponseDTO r=new UserResponseDTO();
		r.setName(user.getName());
		r.setEmail(user.getEmail());
		r.setId(user.getId());
		r.setSalary(user.getMonthlyIncome());
		return ResponseEntity.ok(r);
    }
    
    @PutMapping("/income/{id}")
    public ResponseEntity<UserResponseDTO> updateIncome(@PathVariable Long id,@RequestParam BigDecimal salary)
    {
    	UserResponseDTO resp= service.updateIncome(id, salary);
    	return ResponseEntity.ok(resp);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,@Valid @RequestBody UserUpdateDTO upd)
    {
    	UserResponseDTO resp= service.updateUser(id, upd);
    	return ResponseEntity.ok(resp);
    }
    @GetMapping("/expenses/{id}")
    public ResponseEntity<List<Expense>> getUserExpenses(@PathVariable Long id)
    {
    	List<Expense> expenses=service.getUserExpenses(id);
    	 return ResponseEntity.ok(expenses);
    }

}
