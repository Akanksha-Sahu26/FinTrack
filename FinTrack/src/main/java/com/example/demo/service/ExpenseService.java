package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CategoryResponseDTO;
import com.example.demo.dto.ExpenseRequestDTO;
import com.example.demo.dto.ExpenseResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Expense;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ExpenseRepository;

import jakarta.transaction.Transactional;

@Service
public class ExpenseService {
	private ExpenseRepository repo;
	private UserService userService;
	private final CategoryService categoryService;

    @Autowired
    public ExpenseService(ExpenseRepository repo,
                          UserService userService,
                          CategoryService categoryService) {
        this.repo = repo;
        this.userService = userService;
        this.categoryService = categoryService;
    }
	
    @Transactional
    public ExpenseResponseDTO createExpense(Long userId,ExpenseRequestDTO req)
    {
    	User user = userService.getUserById(userId);

        Category category = categoryService.findOrCreateCategory(req.getCategoryName());
        CategoryResponseDTO cresp=new CategoryResponseDTO(category.getId(), category.getName());
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setCategory(category);
        expense.setAmount(req.getAmt());
        expense.setExpenseDate(
                req.getExpDate() != null ? req.getExpDate() : LocalDate.now()
        );

        Expense saved = repo.save(expense);
        
        ExpenseResponseDTO resp = new ExpenseResponseDTO();
        resp.setId(saved.getId());
        resp.setAmt(saved.getAmount());
        resp.setCategory(cresp);
        resp.setDate(saved.getExpenseDate());

        return resp;
    }

	public List<ExpenseResponseDTO> getAllExpenses(Long userId)
	{
		User user=userService.getUserById(userId);
		List<Expense> expenses= repo.findByUser_Id(user.getId());
		List<ExpenseResponseDTO> resp=new ArrayList<>();
		for(Expense exp:expenses)
		{
			Category category=exp.getCategory();
			ExpenseResponseDTO obj=new ExpenseResponseDTO();
			obj.setAmt(exp.getAmount());
			obj.setCategory(new CategoryResponseDTO(category.getId(), category.getName()));
			obj.setDate(exp.getExpenseDate());
			obj.setId(exp.getId());
			resp.add(obj);
		}
		return resp;
	}
	
	public ExpenseResponseDTO getExpenseById(Long expid,Long userId)
	{
		Expense exp=repo.findByIdAndUserId(expid, userId).
				orElseThrow(()->new ResourceNotFoundException("Expense with this id does not exists"));
		ExpenseResponseDTO obj=new ExpenseResponseDTO();
		Category category=exp.getCategory();
		obj.setAmt(exp.getAmount());
		obj.setCategory(new CategoryResponseDTO(category.getId(), category.getName()));
		obj.setDate(exp.getExpenseDate());
		obj.setId(exp.getId());
		return obj;
	}
	
	@Transactional
	public ExpenseResponseDTO deleteExpenseById(Long expid,Long userId)
	{
		Expense exp=repo.findByIdAndUserId(expid, userId).
				orElseThrow(()->new ResourceNotFoundException("Expense with this id does not exists"));
		User user = exp.getUser();
	    user.getExpenses().remove(exp);
		ExpenseResponseDTO obj=new ExpenseResponseDTO();
		Category category=exp.getCategory();
		obj.setAmt(exp.getAmount());
		obj.setCategory(new CategoryResponseDTO(category.getId(), category.getName()));
		obj.setDate(exp.getExpenseDate());
		obj.setId(exp.getId());
		repo.delete(exp);
		return obj;
	}
	
	public List<ExpenseResponseDTO> getExpensesByCategory(Long userId,String categoryName)
	{
		List<Expense> expenses =
	            repo.findByUser_IdAndCategoryNameIgnoreCase(userId, categoryName);

	    if (expenses.isEmpty()) {
	        throw new ResourceNotFoundException(
	                "No expenses found for category: " + categoryName
	        );
	    }
		List<ExpenseResponseDTO> resp=new ArrayList<>();
		for(Expense exp:expenses)
		{
			ExpenseResponseDTO obj=new ExpenseResponseDTO();
			obj.setAmt(exp.getAmount());
			Category category=exp.getCategory();
			obj.setCategory(new CategoryResponseDTO(category.getId(), category.getName()));
			obj.setDate(exp.getExpenseDate());
			obj.setId(exp.getId());
			resp.add(obj);
		}
		return resp;
	}
	
	public List<ExpenseResponseDTO> getExpensesInDateRange(Long userId,LocalDate start,LocalDate end)
	{
		if (start == null) {
	        throw new BadRequestException("Start date must be provided");
	    }

	    if (end == null) {
	        end = LocalDate.now();
	    }

	    if (start.isAfter(end)) {
	        throw new BadRequestException("Start date cannot be after end date");
	    }
		List<Expense> expenses=repo.findByUser_IdAndExpenseDateBetweenOrderByExpenseDateDesc(userId, start, end);
	    
		List<ExpenseResponseDTO> resp=new ArrayList<>();
		for(Expense exp:expenses)
		{
			ExpenseResponseDTO obj=new ExpenseResponseDTO();
			obj.setAmt(exp.getAmount());
			Category category=exp.getCategory();
			obj.setCategory(new CategoryResponseDTO(category.getId(), category.getName()));
			obj.setDate(exp.getExpenseDate());
			obj.setId(exp.getId());
			resp.add(obj);
		}
		return resp;
	}
	
	public List<ExpenseResponseDTO> getExpensesByDate(
	        Long userId,
	        LocalDate date
	) {
	    if (date == null) {
	        throw new BadRequestException("Date must be provided");
	    }

	    return getExpensesInDateRange(userId, date, date);
	}

	public List<ExpenseResponseDTO> getExpensesByMonth(
	        Long userId,
	        int year,
	        int month
	) {
	    if (month < 1 || month > 12) {
	        throw new BadRequestException("Invalid month value");
	    }

	    LocalDate start = LocalDate.of(year, month, 1);
	    LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

	    return getExpensesInDateRange(userId, start, end);
	}
     
	
	public List<ExpenseResponseDTO> searchByCategoryTerm(Long id, String term)
	{
		if(term==null || term.trim().isEmpty())
		{
			throw new BadRequestException("Term is empty");
		}
		List<Expense> expenses =repo.findByUser_IdAndCategory_NameContainingIgnoreCaseOrderByExpenseDateDesc(id,term.trim());
		List<ExpenseResponseDTO> resp=new ArrayList<>();
		for(Expense exp:expenses)
		{
			ExpenseResponseDTO obj=new ExpenseResponseDTO();
			obj.setAmt(exp.getAmount());
			Category category=exp.getCategory();
			obj.setCategory(new CategoryResponseDTO(category.getId(), category.getName()));
			obj.setDate(exp.getExpenseDate());
			obj.setId(exp.getId());
			resp.add(obj);
		}
		return resp;
	}
	
	public BigDecimal getMonthlySummary(Long userId) {
		
		 LocalDate now = LocalDate.now();
	        LocalDate startOfMonth = now.withDayOfMonth(1);
	        LocalDate endOfMonth = now;

	        BigDecimal total =
	                repo.sumAmountByUserAndExpenseDateBetween(
	                        userId,
	                        startOfMonth,
	                        endOfMonth
	                );

	        // IMPORTANT: aggregate queries can return null
	        return total != null ? total : BigDecimal.ZERO;
	}
	
	public Page<ExpenseResponseDTO> getRecentExpenses(
	        Long userId,
	        int page,
	        int size
	) {
	    if (page < 0 || size <= 0) {
	        throw new BadRequestException("Invalid pagination parameters");
	    }

	    Pageable pageable = PageRequest.of(page, size);

	    Page<Expense> expensesPage =
	            repo.findByUser_IdOrderByExpenseDateDesc(userId, pageable);

	    return expensesPage.map(exp -> {
	        ExpenseResponseDTO dto = new ExpenseResponseDTO();
	        dto.setId(exp.getId());
	        dto.setAmt(exp.getAmount());
	        dto.setDate(exp.getExpenseDate());
	        Category category=exp.getCategory();
			dto.setCategory(new CategoryResponseDTO(category.getId(), category.getName()));
	        return dto;
	    });
	}

}
