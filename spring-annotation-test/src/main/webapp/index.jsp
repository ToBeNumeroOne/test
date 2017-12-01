<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>  
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">  
<html>  
  <head>  
    <title>测试</title>  
    <script type="text/javascript" src="js/jquery-1.10.1.min.js"></script>
    <script type="text/javascript" src="js/jquery.validate.min.js"></script>
    <script type="text/javascript">
   		jQuery.validator.addMethod("wordFilter", function(value, element) {
   			$(element).keyup(function(){return false;});
   			var key = true;
			$.ajax({
				url:"${pageContext.request.contextPath }/test/hasKeyWord", 
				async:false,
				type:"post", 			 
				data: {"content": value},
				success: function(data){
					key = data === true || data === "true";
				},
				error:function(){
					alert("服务器繁忙！");
				}
			});
			return this.optional(element) || key;
		}, "您输入的内容包含敏感词汇，请重新输入！");

    	$(function(){
    		$("#fm").validate({
    			rules : {
    				"content" : {
    					required : true,
    					maxlength : 50,
    					wordFilter : true
    				}
    			},
    			messages : {
    				"content" : {required : "标题不能为空",maxlength :"标题不能超过50个字符",wordFilter:"包含敏感词，请重新输入"},
    			},
    			errorPlacement: function (error, element) { 
    				if (element.attr("name") == "content") {
    			     	error.insertAfter(".content_tip");
    				} else {
    					error.insertAfter(element);
    				}
    	 		} 
    		});
    		
    	});
    </script>
  </head>  
    
  <body>  
    <form id="fm" action="${pageContext.request.contextPath }/test/show" method="post">
    	<input name="content"><font class="content_tip" color="red"></font>
    	<br/>
    	<input type="submit" value="提交">
    </form>   
  </body>  
</html> 