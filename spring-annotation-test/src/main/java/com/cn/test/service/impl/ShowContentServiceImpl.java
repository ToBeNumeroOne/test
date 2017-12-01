package com.cn.test.service.impl;

import org.springframework.stereotype.Service;

import com.cn.test.annotation.MyAnnotation;
import com.cn.test.service.ShowContentService;

@Service
public class ShowContentServiceImpl implements ShowContentService {

	@MyAnnotation(params={"content"})
	public String show(String content) {
		return content;
	}

}
