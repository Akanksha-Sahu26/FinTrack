package com.example.demo.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ExpenseRequestDTO;
import com.example.demo.dto.ExpenseResponseDTO;
import com.example.demo.exception.BadRequestException;
import com.example.demo.service.ExpenseService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {
	private ExpenseService service;
    
	@Autowired
	public ExpenseController(ExpenseService service) {
		super();
		this.service = service;
	}
	
	@PostMapping("/create")
	public ResponseEntity<ExpenseResponseDTO> createExpense(@Valid @RequestBody ExpenseRequestDTO req,HttpSession session)
	{
		 System.out.println("---- CONTROLLER ENTRY ----");
		    System.out.println("Category name: " + req.getCategoryName());
		    System.out.println("Amount: " + req.getAmt());
		    System.out.println("Expense Date: " + req.getExpDate());
	   Long userId=(Long)session.getAttribute("userId");	
	   ExpenseResponseDTO exp=service.createExpense(userId, req);
	   return ResponseEntity.status(HttpStatus.CREATED).body(exp);
	}
	
	@GetMapping
	public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses(HttpSession session)
	{
	   Long userId=(Long)session.getAttribute("userId");	
	   return ResponseEntity.ok(service.getAllExpenses(userId));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ExpenseResponseDTO> getExpenseById(@PathVariable Long id,HttpSession session)
	{
	   Long userId=(Long)session.getAttribute("userId");	
	   ExpenseResponseDTO exp=service.getExpenseById(id, userId);
	   return ResponseEntity.status(HttpStatus.OK).body(exp);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ExpenseResponseDTO> deleteExpenseById(@PathVariable Long id,HttpSession session)
	{
	   Long userId=(Long)session.getAttribute("userId");	
	   ExpenseResponseDTO exp=service.deleteExpenseById(id, userId);
	   return ResponseEntity.ok(exp);

	}
	
	@GetMapping("/category")
	public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByCategoryName(
	        @RequestParam String name,
	        HttpSession session)
	{
	    if (name == null || name.isBlank()) {
	        throw new BadRequestException("Category name cannot be empty");
	    }

	    Long userId = (Long) session.getAttribute("userId");
	    return ResponseEntity.ok(
	            service.getExpensesByCategory(userId, name.trim())
	    );
	}
	
	@GetMapping("/date-range")
	public ResponseEntity<List<ExpenseResponseDTO>> getExpensesInDateRange(
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
	        HttpSession session)
	{
	    Long userId = (Long) session.getAttribute("userId");
	    return ResponseEntity.ok(service.getExpensesInDateRange(userId, start, end));
	}
	
	@GetMapping("/date")
	public ResponseEntity<List<ExpenseResponseDTO>> getExpensesOfDate(
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,HttpSession session)
	{
	    Long userId = (Long) session.getAttribute("userId");
	    return ResponseEntity.ok(service.getExpensesByDate(userId, date));
	}
   
	@GetMapping("/recent-expenses")
	public ResponseEntity<Page<ExpenseResponseDTO>> getRecentExpenses(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size,
	        HttpSession session
	) {
	    Long userId = (Long) session.getAttribute("userId");
	    Page<ExpenseResponseDTO> response =
	            service.getRecentExpenses(userId, page, size);

	    return ResponseEntity.ok(response);
	}

	@GetMapping("/monthly-summary")
	public ResponseEntity<BigDecimal> getMonthlySummary(HttpSession session)
	{
		Long userId = (Long) session.getAttribute("userId");
		BigDecimal amt=service.getMonthlySummary(userId);
		return ResponseEntity.ok(amt);
	}
	
	@GetMapping("/category-term-search")
	public ResponseEntity<List<ExpenseResponseDTO>> searchByCategoryTerm(
	        @RequestParam String term,HttpSession session)
	{
	    Long userId = (Long) session.getAttribute("userId");
	    return ResponseEntity.ok(service.searchByCategoryTerm(userId, term));
	}
	
	@GetMapping("/month")
	public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByMonth(
	        @RequestParam(required = false) Integer year,
	        @RequestParam(required = false) Integer month,
	        HttpSession session
	) {
	    Long userId = (Long) session.getAttribute("userId");

	    LocalDate now = LocalDate.now();

	    int finalYear = (year != null) ? year : now.getYear();
	    int finalMonth = (month != null) ? month : now.getMonthValue();

	    if (finalMonth < 1 || finalMonth > 12) {
	        throw new BadRequestException("Month must be between 1 and 12");
	    }

	    List<ExpenseResponseDTO> expenses =
	            service.getExpensesByMonth(userId, finalYear, finalMonth);

	    return ResponseEntity.ok(expenses);
	}

   
	
}
