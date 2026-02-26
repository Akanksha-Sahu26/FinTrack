package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.interceptor.SessionAuthInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	private SessionAuthInterceptor sessionAuthInterceptor;

	@Autowired
	public WebConfig(SessionAuthInterceptor sessionAuthInterceptor) {
		super();
		this.sessionAuthInterceptor = sessionAuthInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(sessionAuthInterceptor)
				.addPathPatterns("/api/**")
				.excludePathPatterns("/api/auth/**", "/error", "/favicon.ico");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
		registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
		registry.addResourceHandler("/html/**").addResourceLocations("classpath:/static/html/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/html/index.html");
	}
}
