package com.example.demo.interceptor;

import java.io.PrintWriter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionAuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception 
	{
		HttpSession session=request.getSession(false);
		System.out.println("Path: "+request.getRequestURI());
		System.out.println("Method: "+request.getMethod());
		System.out.println("Session present? "+(session!=null));
		if(session!=null)
		{
			System.out.println("Session id:"+session.getId());
			System.out.println("User id:"+session.getAttribute("userId"));
		}
		if(session==null || session.getAttribute("userId")==null)
		{
			response.setStatus(401); // unauthorized
			response.setContentType("application/json");
			PrintWriter pWriter=response.getWriter();
			pWriter.write("{\"error\" : \"Please login first\"}");
			return false;
		}
		Long userIdLong=(Long)session.getAttribute("userId");
		String userRoleString=(String)session.getAttribute("userRole");
		request.setAttribute("currentUserId", userIdLong);
		request.setAttribute("currentUserRole", userRoleString);
		String path=request.getRequestURI();
		String method=request.getMethod();
		/*if(path.startsWith("/api/categories"))
		{
			if(!method.equals("GET") && !userRoleString.equals("ADMIN"))
			{
				response.setStatus(403); // forbidden
				response.setContentType("application/json");
				PrintWriter pWriter=response.getWriter();
				pWriter.write("{\"error\" : \"Admin access required\"}");
				return false;
			}
		}*/
		return true;
	}
    
	
	
}
