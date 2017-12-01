package com.cn.test.action;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.cn.test.annotation.MyAnnotation;
import com.cn.test.utils.SensitivewordFilter;


@Aspect
@Component
public class AnnotationTest{
	
	
	/**
	 * 拦截带有MyAnnotation注解的方法并获取指定字段值
	 * @author xietao
	 * @since version 1.0
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.cn.test..*.*(..)) && @annotation(com.cn.test.annotation.MyAnnotation)")
    public Object permissionCheck(ProceedingJoinPoint point) throws Throwable{
		MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        MyAnnotation anno = (MyAnnotation)method.getAnnotation(MyAnnotation.class);
        System.out.println("调用注解的类是："+ point.getThis());
        System.out.println("注解中传入的参数类型是：name:"+ anno.name()+",params:" + anno.params());
        Object[] objs = point.getArgs();
        String[] strs = anno.params();
        try{
	        for(int i=0;i<objs.length;i++){
	        	Object o = objs[i];
	        	if(o == null) break;
	        	Class c = o.getClass();
	        	Field f = null;
	        	String s = o.getClass().getSimpleName();
	        	if(anno.name().trim().equals("") && strs.length == 1 && s.equals("String")){
	    			System.out.println("要过滤的字符串是：" + o);
	    			String str = SensitivewordFilter.dfa.searchKeyword((String)o);
	    			objs[i] = str;
	    			System.out.println("过滤后的字符串是：" + str);
	    			return point.proceed(objs);
	        	}else if(s.equals(anno.name())){
	        		for(String ss:strs){
	        			if("".equals(ss.trim())) break;
		        		String[] param = ss.split("\\.");
		        		Object temp = o;
		        		if(param.length == 1)
		        			f = c.getDeclaredField(param[0]);
		        		else{
		        			Class c2 = null;
		        			for(int j=1;j<param.length;j++){
				        		f = c.getDeclaredField(param[j-1]);
				        		c2 = f.getType();
				        		f.setAccessible(true);
				        		temp = f.get(temp);
		        			}
		        			if(c2 != null)
		        				f = c2.getDeclaredField(param[param.length-1]);
		        		}
		        		f.setAccessible(true);
		        		System.out.println("要过滤的字符串是：" + f.get(temp));
		        		String content = "";
		        		if(f.getType() == String.class){
		        			content = (String)f.get(temp);
		        			content = SensitivewordFilter.dfa.searchKeyword(content);
		        			f.set(temp, content);
		        		}
		    			System.out.println("过滤后的字符串是：" + f.get(temp));
	        		}
	        	}
	        }
	        System.out.println("-----------------过滤完成!------------------------------");
        }catch(Exception e){
        	e.printStackTrace();
        }
        return point.proceed();
    }
}
