package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CategoryResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CategoryRepository;



@Service
public class CategoryService {
	private CategoryRepository repo;
    @Autowired
	public CategoryService(CategoryRepository repo) {
		super();
		this.repo = repo;
	}
    
    public CategoryResponseDTO createCat(String name)
    {
    	if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        String normalizedName = name.trim().toLowerCase();

        if (repo.existsByName(normalizedName)) {
            throw new ResourceAlreadyExistsException("Category already exists");
        }

        Category cat = new Category();
        cat.setName(normalizedName);

        Category saved = repo.save(cat);

        return new CategoryResponseDTO(saved.getId(), saved.getName());
    }
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id)
    {
    	Category cat= repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category with id " + id + " does not exist"));	
    	CategoryResponseDTO resp = new CategoryResponseDTO(cat.getId(),cat.getName());
    	return resp;
    }
    public List<CategoryResponseDTO> getAllCategories()
    {
    	List<Category> categories=repo.findAll();
    	List<CategoryResponseDTO> res=new ArrayList<>();
    	for(Category cat:categories)
    	{
    		CategoryResponseDTO resp = new CategoryResponseDTO(cat.getId(),cat.getName());
    		res.add(resp);

    	}
    	return res;
    }
	 
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> searchCategories(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCategories();
        }

        List<Category> categories =
                repo.findByNameContainingIgnoreCase(keyword.trim());

        List<CategoryResponseDTO> response = new ArrayList<>();

        for (Category cat : categories) {
            response.add(new CategoryResponseDTO(cat.getId(), cat.getName()));
        }

        return response;
    }
    
    public Category findOrCreateCategory(String name) 
    {
    	if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        String normalized = name.trim().toLowerCase();
        Category category= repo.findByNameIgnoreCase(normalized);
        if(category==null)
        {
        	Category cat = new Category();
            cat.setName(normalized);
            return repo.save(cat);
        }
        return category;        
    }
    
   /* public Category findCategory(String name) 
    {
    	if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        String normalized = name.trim();
        Category category= repo.findByNameIgnoreCase(normalized);
        return category;        
    }
    */
}
