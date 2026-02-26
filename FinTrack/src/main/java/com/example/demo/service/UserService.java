package com.example.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserResponseDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.Expense;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	private UserRepository userRepo;
    @Autowired
	public UserService(UserRepository userRepo) {
		super();
		this.userRepo = userRepo;
	}
    
    public List<UserResponseDTO> getAllUsers()
    {
    	List<User> users= userRepo.findAll();
    	List<UserResponseDTO> resp=new ArrayList<>();
    	for(User user:users)
    	{
    		UserResponseDTO r=new UserResponseDTO();
    		r.setName(user.getName());
    		r.setEmail(user.getEmail());
    		r.setId(user.getId());
    		r.setSalary(user.getMonthlyIncome());
    		resp.add(r);
    	}
    	return resp;
    }
    
    public User getUserById(Long id)
    {
    	 return userRepo.findById(id)
                 .orElseThrow(() ->
                         new ResourceNotFoundException("User with id " + id + " does not exist"));
    }
    @Transactional
	public UserResponseDTO updateIncome(Long id,BigDecimal salary)
	{
		if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income must be greater than zero");
        }
		User user=getUserById(id);
		user.setMonthlyIncome(salary);
		userRepo.save(user);
		UserResponseDTO resp=new UserResponseDTO();
		resp.setName(user.getName());
		resp.setEmail(user.getEmail());
		resp.setId(user.getId());
		resp.setSalary(salary);
		return resp;
	}
	@Transactional
	public UserResponseDTO updateUser(Long id,UserUpdateDTO upd)
	{
		User user=getUserById(id);
		if(upd==null || (upd.getName()==null && upd.getEmail()==null && upd.getPassword()==null ))
		{
			throw new IllegalArgumentException("At least one field must be provided for update");
		}
		if(upd.getName()!=null)
		{
			user.setName(upd.getName());
		}
		if(upd.getPassword()!=null)
		{
			user.setPassword(upd.getPassword());
		}
		if(upd.getEmail()!=null)
		{
			if(userRepo.existsByEmail(upd.getEmail()))
			{
				throw new ResourceAlreadyExistsException("This email already exists");
			}
			user.setEmail(upd.getEmail());
		}
		User respUser=userRepo.save(user);
		UserResponseDTO resp=new UserResponseDTO();
		resp.setName(respUser.getName());
		resp.setEmail(respUser.getEmail());
		resp.setId(respUser.getId());
		resp.setSalary(respUser.getMonthlyIncome());
		return resp;
	}
	@Transactional
	public List<Expense> getUserExpenses(Long id)
	{
		User user=getUserById(id);
    	return user.getExpenses();
	}

}
