package com.example.demo.exception;

import lombok.Data;

@Data

public class ErrorResponse {
   
   private int status;
   private String msg;
public ErrorResponse(int status, String msg) {
	super();
	this.status = status;
	this.msg = msg;
}
   
}
