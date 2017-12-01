package com.cn.test.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.cn.test.utils.SensitivewordFilter;

public class SysStrutsFilter implements Filter {


	public void init(FilterConfig filterConfig) throws ServletException {
		// 加载系统缓存信息
		System.out.println("加载系统敏感词库...");
		SensitivewordFilter.init();
	}

	public void destroy() {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);
	}

}
