package com.example.demo.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense,Long>{
	List<Expense> findByUser_Id(Long userId);
	Optional<Expense> findByIdAndUserId(Long expenseId, Long userId);
	List<Expense> findByUser_IdAndCategoryNameIgnoreCase(
	        Long userId,
	        String categoryName
	);
	List<Expense> findByUser_IdAndExpenseDateBetweenOrderByExpenseDateDesc(
	        Long userId,
	        LocalDate start,LocalDate end
	);
	List<Expense> findByUser_IdAndCategory_NameContainingIgnoreCaseOrderByExpenseDateDesc(
	        Long userId,
	        String name
	);

	@Query("""
		    SELECT COALESCE(SUM(e.amount), 0)
		    FROM Expense e
		    WHERE e.user.id = :userId
		      AND e.expenseDate BETWEEN :start AND :end
		""")
		BigDecimal sumAmountByUserAndExpenseDateBetween(
		        @Param("userId") Long userId,
		        @Param("start") LocalDate start,
		        @Param("end") LocalDate end
		);

	Page<Expense> findByUser_IdOrderByExpenseDateDesc(
	        Long userId,
	        Pageable pageable
	);
}
