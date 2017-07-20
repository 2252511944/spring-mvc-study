package com.imooc.mvcdemo.service;

import org.springframework.stereotype.Service;

import com.imooc.mvcdemo.model.Course;

@Service
public interface CourseService {
	
	// 业务逻辑，根据id获取课程
	Course getCoursebyId(Integer courseId);

}
