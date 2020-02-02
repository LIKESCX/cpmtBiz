package com.cpit.cpmt.biz.utils.validate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Type {

	private static String not_null="不能为空!";
	private static String not_enum="无效枚举值!";
	private static String not_date="无效时间格式!";
	private static String not_valid="无效参数类型!";
	private static String not_lenght="长度不正确!";
	private static String not_code="上传数据项编码长度不正确!";
	
	public static String date_time = "(((01[0-9]{2}|0[2-9][0-9]{2}|[1-9][0-9]{3})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|((01[0-9]{2}|0[2-9][0-9]{2}|[1-9][0-9]{3})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|((01[0-9]{2}|0[2-9][0-9]{2}|[1-9][0-9]{3})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((04|08|12|16|[2468][048]|[3579][26])00))-0?2-29)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d";
	public static String date_ymd = "(19|20)[0-9][0-9]-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])";
	
	public static Boolean check(String s,String p){
		 if(s.length()==19 || s.length()==10){
		 }else{
			 return false; 
		 }
		 Pattern a=Pattern.compile(p); 
	     Matcher b=a.matcher(s); 
	     if(b.matches()) {
	           return true;
	     } else {
	           return false;
	     }
	 }
	
	
	public String _String(String isNull,String comment,Object name,String value,int positon,HashMap enumMap,String format,int size, int maxsize){
		
		positon++;
		String pos = (positon>0)?",所在位置：第"+positon+"条数据;":";";
		if(name == null || name.toString().length()==0){
			 if("no".equals(isNull)){
				 return comment+"["+value+"]:"+not_null + pos;
			 }
		 }else{
			 if(size!=-1){
				 if(name.toString().length()!=size){
					 return comment+"["+value+"]:"+not_lenght + pos;
				 } 
			 }
			 if(maxsize!=-1){
				 if(name.toString().length()>maxsize){
					 return comment+"["+value+"]:"+not_lenght + pos;
				 } 
			 }
		 }
		return "";
	}
	
	public String _Boolean(String isNull,String comment,Object name,String value,int positon,HashMap enumMap,String format,int size, int maxsize){
		
		positon++;
		String pos = (positon>0)?",所在位置：第"+positon+"条数据;":";";
		if(!(name instanceof Boolean)){
			 return comment+"["+value+"]:"+not_valid + pos;
		 }
		return "";
	}
	
	public String _Double(String isNull,String comment,Object name,String value,int positon,HashMap enumMap,String format,int size, int maxsize){
		
		positon++;
		String pos = (positon>0)?",所在位置：第"+positon+"条数据;":";";
		if(name instanceof Integer){
			 return "";
		 }
		else if(name instanceof BigDecimal){
			 return "";
		 }
		else if(name instanceof String){
			try{
				Double.valueOf((String)name);
				return "";
			}catch(Exception ex){
			}
		}
		return comment+"["+value+"]:"+not_valid + pos;
	}
	
	public String _int(String isNull,String comment,Object name,String value,int positon,HashMap enumMap,String format,int size, int maxsize){
		
		positon++;
		String pos = (positon>0)?",所在位置：第"+positon+"条数据;":";";
		if(name instanceof Integer){
			return "";
		}else if(name instanceof String){
			try{
				Integer.valueOf((String)name);
				return "";
			}catch(Exception ex){
				
			}
		}
	    return comment+"["+value+"]:"+not_valid + pos;
	}
	
	public String _enum(String isNull,String comment,Object name,String value,int positon,HashMap enumMap,String format,int size, int maxsize){
		
		positon++;
		String pos = (positon>0)?",所在位置：第"+positon+"条数据;":";";
		
		if("String".equals(format)){
			if(name instanceof String){
				String data = (String) name;
				if(enumMap.get(data) == null || enumMap.get(data).toString().length() == 0){
					 return comment+"["+value+"]:"+not_enum + pos;
				 }
			}else{
				return comment+"["+value+"]:"+not_valid + pos;
			}
		}
		if("int".equals(format)){
			if(name instanceof Integer){
				Integer data = (Integer) name;
				if(enumMap.get(data+"") == null || enumMap.get(data+"").toString().length() == 0){
					 return comment+"["+value+"]:"+not_enum + pos;
				 }
			}else if((name instanceof String)&&!"".equals(name)){
				Integer data = Integer.valueOf((String)name);
				if(enumMap.get(data+"") == null || enumMap.get(data+"").toString().length() == 0){
					 return comment+"["+value+"]:"+not_enum + pos;
				 }
			}else{
				return comment+"["+value+"]:"+not_valid + pos;
			}
		}
		if("boolean".equals(format)){
			if(name instanceof Boolean){
				Boolean  data = (Boolean) name;
				if(enumMap.get(data.toString()) == null || enumMap.get(data.toString()).toString().length() == 0){
					return comment+"["+value+"]:"+not_enum + pos;
				}
			}else if(name instanceof String){
				Boolean data = Boolean.valueOf((String)name);
				if(enumMap.get(data.toString()) == null || enumMap.get(data.toString()).toString().length() == 0){
					 return comment+"["+value+"]:"+not_enum + pos;
	 		    }
			}else{
				 return comment+"["+value+"]:"+not_valid + pos;
			}
		}
		return "";
	}
	
	
	public String _Date(String isNull,String comment,Object name,String value,int positon,HashMap enumMap,String format,int size, int maxsize){
		
		positon++;
		String pos = (positon>0)?",所在位置：第"+positon+"条数据;":";";
		
		String strName = null;
		if(name != null)
			strName = String.valueOf(name);
				
	    if(strName == null || strName.length() == 0){
			 if("no".equals(isNull)){
				 return comment+"["+value+"]:"+not_null + pos;
			 }else{
				 return ""; 
			 }
		 }
		if(format.equals("yyyy-MM-dd HH:mm:ss")){
			if(!check(strName,date_time))
				return comment+"["+value+"]:"+not_date + pos;
		}else{
			if(!check(strName,date_ymd))
				return comment+"["+value+"]:"+not_date + pos;
		}
		return "";
	}
	
	public String _Code(String isNull,String comment,Object name,String value,int positon,HashMap enumMap,String format,int size, int maxsize){
		
		positon++;
		String pos = (positon>0)?",所在位置：第"+positon+"条数据;":";";
		String strName = null;
		if(name != null)
			strName = String.valueOf(name);
		if(strName == null || (strName).length() == 0){
			 if("no".equals(isNull)){
				 return comment+"["+value+"]:"+not_null + pos;
			 }else{
				 return ""; 
			 }
		 }
		if((strName).length()!=21){
			return comment+"["+value+"]:"+not_code + pos;
		}
		return "";
	}
	public String _StringArray(String isNull,String comment,Object name,String value,int positon,HashMap enumMap,String format,int size, int maxsize){
		
		positon++;
		String pos = (positon>0)?",所在位置：第"+positon+"条数据;":";";
		if(name == null || name.toString().length()==0){
			 if("no".equals(isNull)){
				 return comment+"["+value+"]:"+not_null + pos;
			 }
		 }else{
			 if (!name.toString().startsWith("[")||!name.toString().endsWith("]") ) {
				 return comment+"["+value+"]:"+not_valid + pos;
		        }
			 
		 }
		return "";
	}

}
