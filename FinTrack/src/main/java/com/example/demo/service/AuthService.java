package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class AuthService {
	private UserRepository userRepo;
    
	@Autowired
	public AuthService(UserRepository userRepo) {
		super();
		this.userRepo = userRepo;
	}
	public AuthResponseDto register(RegisterRequestDTO req)
	{
	   if(userRepo.existsByEmail(req.getEmail()))
	   {
		   throw new ResourceAlreadyExistsException("Email already registered");
	   }
	   User user=new User();
	   user.setName(req.getName());
	   user.setEmail(req.getEmail());
	   user.setMonthlyIncome(req.getSalary());
	   user.setPassword(req.getPassword());
	   userRepo.save(user);
	   return new AuthResponseDto(
	    		user.getId(),
	    		user.getName(),
	    		user.getEmail(),
	            "Registration successfull");
	   }
	@Transactional
	public AuthResponseDto login(LoginRequestDTO request, HttpSession session) {

	    // Step 1: Find user by email
	    User user = userRepo.findByEmail(request.getEmail())
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Invalid email or password")
	            );

	    // Step 2: Check password
	    if (!user.getPassword().equals(request.getPassword())) {
	        throw new ResourceNotFoundException("Invalid email or password");
	    }

	    // Step 3: Password correct - Create session
	    session.setAttribute("userId", user.getId());
	    session.setAttribute("userName", user.getName());
	    session.setAttribute("userEmail", user.getEmail());

	    // Step 4: Return response
	    return new AuthResponseDto(
	    		user.getId(),
	    		user.getName(),
	    		user.getEmail(),
	            "Login successful"
	    );
	}
	public void logout(HttpSession session) {
		 Long userId = (Long) session.getAttribute("userId");

	    if (userId == null) {
	        throw new BadRequestException("You are not logged in");
	    }

	    session.invalidate();
	}

    public AuthResponseDto getCurrentUser(HttpSession session) {

        // Get user info from session
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new ResourceNotFoundException("No user logged in");
        }

        String userName = (String) session.getAttribute("userName");
        String userEmail = (String) session.getAttribute("userEmail");
       

        return new AuthResponseDto(
                userId,
                userName,
                userEmail,
                "Current user info"
        );
    }

}
