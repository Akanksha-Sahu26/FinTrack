package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private AuthService serv;
    @Autowired
	public AuthController(AuthService serv) {
		super();
		this.serv = serv;
	}
    
    @PostMapping("/reg")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid RegisterRequestDTO obj)
    {
    	AuthResponseDto authResponseDto=serv.register(obj);
    	return new ResponseEntity<AuthResponseDto>(authResponseDto, HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginRequestDTO obj,HttpSession session)
    {
    	AuthResponseDto authResponseDto=serv.login(obj, session);
    	return new ResponseEntity<AuthResponseDto>(authResponseDto, HttpStatus.OK);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        serv.logout(session);
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDto> getCurrentUser(HttpSession session) {
        AuthResponseDto response = serv.getCurrentUser(session);
        return ResponseEntity.ok(response);
    }
	

}
