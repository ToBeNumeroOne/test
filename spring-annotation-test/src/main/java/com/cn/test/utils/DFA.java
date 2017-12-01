package com.cn.test.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.cn.test.bo.TreeNode;


/** 
 * @author 徐良永 
 * @Date   2011-10-13 上午9:23:43 
 */  
public class DFA {  
      
    /** 
     * 根节点 
     */  
    private TreeNode rootNode = new TreeNode();  
      
    /** 
     * 关键词缓存 
     */  
    private ByteBuffer keywordBuffer = ByteBuffer.allocate(1024);     
      
    /** 
     * 关键词编码 
     */  
    private String charset = "utf-8"; 
    
    /**
	 * 读取敏感词库中的内容，将内容添加到list集合中
	 * @version 1.0
	 * @throws Exception 
	 */
	@SuppressWarnings("resource")
    public List<String> readSensitiveWordFile() throws Exception{
    	List<String> list = null;
		
		File file = new File("D:\\test\\SensitiveWord.txt");    //读取文件
		InputStreamReader read = new InputStreamReader(new FileInputStream(file),charset);
		try {
			if(file.isFile() && file.exists()){      //文件流是否存在
				list = new ArrayList<String>();
				BufferedReader bufferedReader = new BufferedReader(read);
				String txt = null;
				while((txt = bufferedReader.readLine()) != null){    //读取文件，将文件内容放入到set中
					list.add(txt);
			    }
			}
			else{         //不存在抛出异常信息
				throw new Exception("敏感词库文件不存在");
			}
		} catch (Exception e) {
			throw e;
		}finally{
			read.close();     //关闭文件流
		}
		return list;
	}
  
    /** 
     * 创建DFA 
     * @param keywordList 
     * @throws UnsupportedEncodingException  
     */  
    public void createKeywordTree(List<String> keywordList) throws UnsupportedEncodingException{  
          
        for (String keyword : keywordList) {  
            if(keyword == null) continue;  
            keyword = keyword.trim();  
            byte[] bytes = keyword.getBytes(charset);  
              
            TreeNode tempNode = rootNode;  
            //循环每个字节  
            for (int i = 0; i < bytes.length; i++) {  
                int index = bytes[i] & 0xff; //字符转换成数字  
                TreeNode node = tempNode.getSubNode(index);  
                  
                if(node == null){ //没初始化  
                    node = new TreeNode();  
                    tempNode.setSubNode(index, node);  
                }  
                  
                tempNode = node;  
                  
                if(i == bytes.length - 1){  
                    tempNode.setKeywordEnd(true);    //关键词结束， 设置结束标志  
                }  
            }//end for  
            System.gc();
        }//end for  
          
    } 
    
    public boolean hasKeyWord(String text) throws UnsupportedEncodingException {
    	return hasKeyWord(text.getBytes(charset));
    }
    private boolean hasKeyWord(byte[] bytes) throws UnsupportedEncodingException {
        TreeNode tempNode = rootNode;  
        int rollback = 0;   //回滚数  
        int position = 0; //当前比较的位置  
        while (position < bytes.length) {  
            int index = bytes[position] & 0xFF;  
            tempNode = tempNode.getSubNode(index);  
            
            //当前位置的匹配结束  
            if(tempNode == null){   
                position = position - rollback; //回退 并测试下一个字节  
                rollback = 0;  
                tempNode = rootNode;    //状态机复位  
            } else if(tempNode.isKeywordEnd()){  //是结束点 记录关键词  
            	return true;	
            } else{
                rollback++; //非结束点 回退数加1  
            }  
            position++;  
        }  
    	return false;
    }
    
    /** 
     * 添加关键词
     * @param keywordList 
     * @throws UnsupportedEncodingException  
     */  
    public void addKeyword(String keyword) throws UnsupportedEncodingException{  
          
        if(keyword == null) return;  
        keyword = keyword.trim();  
        byte[] bytes = keyword.getBytes(charset);  
          
        TreeNode tempNode = rootNode;  
        //循环每个字节  
        for (int i = 0; i < bytes.length; i++) {  
            int index = bytes[i] & 0xff; //字符转换成数字  
            TreeNode node = tempNode.getSubNode(index);  
              
            if(node == null){ //没初始化  
                node = new TreeNode();  
                tempNode.setSubNode(index, node);  
            }  
              
            tempNode = node;  
              
            if(i == bytes.length - 1){  
                tempNode.setKeywordEnd(true);    //关键词结束， 设置结束标志  
            }  
        }//end for  
          
    }  
      
    /** 
     * 搜索关键字 
     */  
    public String searchKeyword(String text) throws UnsupportedEncodingException{  
        return searchKeyword(text.getBytes(charset),text);  
    }  
      
    /** 
     * 搜索关键字 
     * @throws UnsupportedEncodingException 
     */  
    private String searchKeyword(byte[] bytes,String text) throws UnsupportedEncodingException{  
        TreeNode tempNode = rootNode;  
        int rollback = 0;   //回滚数  
        int position = 0; //当前比较的位置  
        boolean key1 = true,key2 = false;//key1 连续敏感词标记 key2 敏感词标记  
        int keyEnd = 0;//上个敏感词位置
        String keyword = null;//敏感词
        while (position < bytes.length) {  
            int index = bytes[position] & 0xFF;  
            keywordBuffer.put(bytes[position]); //写关键词缓存  
            tempNode = tempNode.getSubNode(index);
            
            //当前位置的匹配结束  
            if(tempNode == null){   
                position = position - rollback; //回退 并测试下一个字节  
                rollback = 0;  
                tempNode = rootNode;    //状态机复位  
                keywordBuffer.clear();  //清空  
                if(key2) {//最长敏感词匹配结束，进行文本替换
                	if(key1) text = text.replaceFirst(keyword,"***"); 
                	else text = text.replaceFirst(keyword,"");
                	key2 = false;
                }
            } else if(tempNode.isKeywordEnd()){  //是结束点 记录关键词  
            	keywordBuffer.flip();  
            	keyword = Charset.forName(charset).decode(keywordBuffer).toString();  
            	keywordBuffer.limit(keywordBuffer.capacity());  
            	key2 = true;
            	if(keyEnd == (position - rollback - 1)) key1 = false;
            	else key1 = true;
            	if(position == bytes.length-1) {//敏感词在句末的处理
            		if(key1) text = text.replaceFirst(keyword,"***"); 
                	else text = text.replaceFirst(keyword,"");
            		keywordBuffer.clear();
            	}
            	keyEnd = position;
                rollback = 1;   //遇到结束点  rollback 置为1  
            } else{
                rollback++; //非结束点 回退数加1  
            }  
              
            position++;  
        }  
        return text;
    } 
      
    public void setCharset(String charset) {  
        this.charset = charset;  
    }  
      
}  