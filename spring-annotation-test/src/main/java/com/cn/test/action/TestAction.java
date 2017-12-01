package com.cn.test.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.cn.test.service.ShowContentService;
import com.cn.test.utils.SensitivewordFilter;

@Controller
@RequestMapping("/test")
public class TestAction {
	
	@Resource
	private ShowContentService contentService;
	
	@RequestMapping("/show")
	public String toIndex(HttpServletRequest request,Model model){
		String content = request.getParameter("content");
		content = contentService.show(content);
		model.addAttribute("content", content);  
		return "show";
	}
	
	@RequestMapping("/hasKeyWord")
	public void hasKeyWord(HttpServletRequest request,Model model,HttpServletResponse response){
		PrintWriter p = null;
		try {
			boolean flag = SensitivewordFilter.dfa.hasKeyWord(request.getParameter("content"));
			p = response.getWriter();
			p.print(!flag);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			p.print(0);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(p!=null) p.close();
		}
	}
	
	public static void main(String[] args) {
		JSONObject jo = new JSONObject();
		String s = "{\"name\":\"aa\"}";
		jo = JSONObject.parseObject(s);
		System.out.println(jo);
	}
	
	public static void t1() {
		System.out.println("t1");
		return;
	}
	
	public static void t2() {
		System.out.println("t2");
		return;
	}
}
