package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Category;
import com.example.demo.entity.Expense;

public interface CategoryRepository extends JpaRepository<Category,Long>{
	public boolean existsByName(String name);
	List<Category> findByNameContainingIgnoreCase(String keyword);
	Category findByNameIgnoreCase(String name);

}
