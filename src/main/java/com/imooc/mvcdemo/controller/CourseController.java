package com.imooc.mvcdemo.controller;

/*
 * 按照慕课网操作：http://www.imooc.com/video/8602
 * 使用jetty启动
 */

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.imooc.mvcdemo.model.Course;
import com.imooc.mvcdemo.service.CourseService;

@Controller // 会被spring的DispatcherServlet上下文所管理，并且完成它的依赖注入（DI）
@RequestMapping("/courses") // /courses都会被Controller所拦截 
public class CourseController {

	// 注意导包slf4j
	private static Logger log = LoggerFactory.getLogger(CourseController.class);
	
	private CourseService courseService;  

	@Autowired
	public void setCourseService(CourseService courseService) {
		this.courseService = courseService;
	}
	
	// 本方法处理/courses/view?courseId=123，但是这种方法好土
	@RequestMapping(value="view", method=RequestMethod.GET)
	public String viewCourse(@RequestParam("courseId")Integer courseId, Model model){
		System.out.println("基础版");
		log.debug("In viewCourse, courseId={}", courseId); //SLF4J可以使用占位符 
		Course course = courseService.getCoursebyId(courseId);
		model.addAttribute(course);
		return "course_overview";
	}
	
	// 来个升级版本的请求,花括号表示是路径变量
	@RequestMapping(value="/view2/{courseId}", method=RequestMethod.GET)
	public String viewCourse2(@PathVariable("courseId")Integer courseId, Map<String, Object> model){
		System.out.println("升级版1");
		log.debug("In viewCourse2, courseId={}"+courseId);
		Course course = courseService.getCoursebyId(courseId);
		model.put("course", course); // 为路径变量赋值
		return "course_overview";
	}
	
	// /courses/view?courseId=456
	@RequestMapping("view3")
	public String viewCourse3(HttpServletRequest httpServletRequest){
		System.out.println(httpServletRequest); // (GET //courses/view3?courseId=456)
		Integer courseId = Integer.valueOf(httpServletRequest.getParameter("courseId"));
		log.debug("In viewCourse2, courseId={}"+courseId);
		Course course = courseService.getCoursebyId(courseId);
		httpServletRequest.setAttribute("course", course);
		return "course_overview";
	}
	
	// 课程添加及保存的维护界面
	@RequestMapping(value = "/admin", method = RequestMethod.GET, params = "add")
	public String createCourse(){
		
		return "course_admin/edit";
	}
	
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String doSave(Course course){
		
		log.debug("info of doSave");
		log.debug(ReflectionToStringBuilder.toString(course));
		// 在此进行业务操作
		course.setCourseId(123);
		// 重定向
		return "redirect:view2/" + course.getCourseId();
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String showUpLoadPage(){
		
		return "course_admin/file";
	}
	
	// 使用spring暴露的接口来传输文件,使用@RequestParam来绑定表单数据
	@RequestMapping(value = "/doUpload", method = RequestMethod.POST)
	public String doUpLoadFile(@RequestParam("file") MultipartFile file) throws IOException{
		
		if(!file.isEmpty()){
			log.debug("Process file+++++++++++++++++++++" + file.getOriginalFilename());
			FileUtils.copyInputStreamToFile(file.getInputStream(), new File("c:\\temp\\imooc", System.currentTimeMillis()+file.getOriginalFilename()));
		}
		return "success";
	}
	
	// 处理json格式的数据@ResponseBody
	@RequestMapping(value="/{courseId}",method=RequestMethod.GET)
	public @ResponseBody Course getCourseInJson(@PathVariable Integer courseId){
		return  courseService.getCoursebyId(courseId);
	}
	// 也可以不使用@ResponseBody注解，直接使用ResponseEntity
	@RequestMapping(value="/jsontype/{courseId}",method=RequestMethod.GET)
	public  ResponseEntity<Course> getCourseInJson2(@PathVariable Integer courseId){
		Course course =   courseService.getCoursebyId(courseId);		
		return new ResponseEntity<Course>(course, HttpStatus.OK);
	}
	
}
