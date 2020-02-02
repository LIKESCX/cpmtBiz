package com.cpit.cpmt.biz.utils.validate;



import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


@SuppressWarnings(value={"unchecked"})
public class Protocol2Parse {
	
	private final static Logger logger = LoggerFactory.getLogger(Protocol2Parse.class);
	
	private static Element rootEle=null;
	/**
	 * 接口参数校验
	 * @param version  版本,目前为V1.0
	 * @param objectname	    接口唯一标识
	 * @param josn	        需要校验的参数
	 * @return
	 */
	public static ReturnCode validate2Parameter(String version,String objectName,String json,String...param) throws Exception{
		
		
		if(rootEle==null) {
			rootEle = processJob();
			if(rootEle==null) {
				logger.error(rootEle+"根元素不存在;");
				throw new Exception(rootEle+"根元素不存在;");
			}
		}
		Element nodeEle = null;//protocol节点

		StringBuffer E4003 = new StringBuffer();//E4003详细信息
		StringBuffer E4004 = new StringBuffer();//E4004详细信息
		
		String nodeId="";
		for (Iterator iter = rootEle.elementIterator(); iter.hasNext();) {
			nodeEle = (Element) iter.next();
			nodeId = nodeEle.attributeValue("id");
			if ((objectName).equals(nodeId)){
				break;
			}{
				nodeId="";
			}
		}
		logger.info("receive protocolId="+nodeId);
		if (nodeId.isEmpty()) {
			logger.error(nodeId+"节点不存在;");
			throw new Exception(nodeId+"节点不存在;");
		}
		
		
		ReturnCode rc = new ReturnCode();
		
		//检查5个参数是否缺失
		/*if(jsonObject.get("OperatorID") == null){
			E4003.append("OperatorID,");
		}
		if(jsonObject.get("Data") == null){
			E4003.append("Data,");
		}
		if(jsonObject.get("TimeStamp") == null){
			E4003.append("TimeStamp,");
		}
		if(jsonObject.get("Seq") == null){
			E4003.append("Seq,");
		}	
		if(jsonObject.get("Sig") == null){
			E4003.append("Sig");
		}*/
		if(E4003.length() != 0){
			rc.setCode(ReturnCode.CODE_4003);
			rc.setErrorMsg("缺少必填项:"+E4003);
			return rc;
		}
		
		//ToDo========= 签名是否合法 in future, put it in a private method
		//==============
		
		//检查Data数据
		Type verify = new Type();
		Class clazz = verify.getClass();
		//判断Data是jsonArray还是jsonObject
		String nodeType = nodeEle.attributeValue("type");
		if (nodeType.isEmpty()) {
			logger.error(nodeType+"节点类型不存在;");
			throw new Exception(nodeType+"节点类型不存在;");
		}
		if("array".equals(nodeType)) {
			String JsonStr = JSON.parseObject(json).getString("Data");
			if("BmsInfos".equals(objectName)||"StationInfos".equals(objectName)) {
				
			}else {
				if(param!=null&&param.length!=0) {
					JsonStr = JSON.parseObject(JsonStr).getString(param[0]);
				}else {
					JsonStr = JSON.parseObject(JsonStr).getString(objectName);
				}
			}
			JSONArray jsonArray = JSONArray.parseArray(JsonStr);
			if(jsonArray.size()>0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					// 遍历 jsonarray 数组，把每一个对象转成 json 对象
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					checkData(jsonObj, nodeEle, E4003, E4004,verify, clazz,-1);
					if(E4003.length()>0||E4004.length()>0) {
						break;
					}
				}
			}
		}else if("object".equals(nodeType)){
			JSONObject jsonObject = (JSONObject) JSONObject.parseObject(json);
			if("BmsInfos".equals(objectName)||"StationInfos".equals(objectName)) {
				jsonObject = jsonObject.getJSONObject("Data");
			}else {
				if(param!=null&&param.length!=0) {
					jsonObject = jsonObject.getJSONObject("Data").getJSONObject(param[0]);
				}else {
					if("QueryFrequency".equals(objectName)) {
						jsonObject = jsonObject.getJSONObject("Data");
					}else {
						jsonObject = jsonObject.getJSONObject("Data").getJSONObject(objectName);
					}
				}
			}
			checkData(jsonObject, nodeEle, E4003, E4004,verify, clazz,-1);
			
		}
		
		String mgs ="";
		rc.setCode(ReturnCode.CODE_OK);
		if(E4003.toString().length()>0){
			rc.setCode(ReturnCode.CODE_4003);
			mgs += "缺少必填项:"+E4003.toString();
		}else if(E4004.toString().length()>0){
			rc.setCode(ReturnCode.CODE_4004);
			mgs += "参数格式不合法:"+E4004.toString();
		}
		rc.setErrorMsg(mgs);
		return rc;
	}
	
	private static String getFormat(Element ele,String checkType) {
		if("_Date".equals(checkType)){
			return ele.attributeValue("format");
		}
		if("_enum".equals(checkType)){
			return ele.attributeValue("format");
		}
		return "";
	}
	
	private static Map<String,String> getEnumMap(Element ele,String checkType) {
		Map<String,String> enumMap = new HashMap<String,String>();
		if("_enum".equalsIgnoreCase(checkType)){
			for (Iterator iter = ele.elementIterator(); iter.hasNext();) {
				Element enumEle = (Element) iter.next();
				enumMap.put(enumEle.attributeValue("name"), enumEle.attributeValue("value"));
			}
		}
		return enumMap;
	}


	private static List<Object> getJson2Name(String attributeValue, JSONArray jsonArray) {
		List<Object> list = new ArrayList<Object>();
		JSONObject  row = null;
		for (int i = 0; i < jsonArray.size(); i++) {  
			 row = jsonArray.getJSONObject(i);
			 list.add(row.get(attributeValue));  
		} 
		return list;
	}
	
	/**
	 * 检查Data数据
	 * @throws Exception
	 */
	private static void checkData(JSONObject jsonObject, Element nodeEle,
			StringBuffer E4003, StringBuffer E4004,
			Type verify, Class clz, int pos
		) throws Exception{

		for (Iterator iter1 = nodeEle.elementIterator(); iter1.hasNext();) {
			Element firstEle = (Element) iter1.next();

			if (firstEle.getName().equals("item")) {
				
				Object name = jsonObject.get(firstEle.attributeValue("name"));
				checkElement(firstEle, name, E4003, E4004, verify, clz, pos);
				if(E4003.length()>0||E4004.length()>0) {
					break;
				}
			}else if (firstEle.getName().equals("object")) {
				Object name = jsonObject.get(firstEle.attributeValue("name"));
				checkElement(firstEle, name, E4003, E4004, verify, clz, pos);
			}else if (firstEle.getName().equals("array")) {
				JSONArray jsonArray = jsonObject.getJSONArray(firstEle.attributeValue("name"));
				if (jsonArray == null && "no".equals(firstEle.attributeValue("isNull"))) {
					E4003.append(firstEle.attributeValue("name") + "[" + firstEle.attributeValue("value") + "];");
					break;
				} else if (jsonArray != null) { // use recursion to analyze array
					for(int i=0;i<jsonArray.size();i++){
						checkData(jsonArray.getJSONObject(i), firstEle, E4003, E4004,verify,clz,i);
						if(E4003.length()>0||E4004.length()>0) {
							break;
						}
					}
					pos = -1;
				}	
			}
		}
	}
	
	/**
	 * 检查每个item element的公用方法
	 * @param element
	 * @param jsonName
	 * @param E4003
	 * @param E4004
	 * @param type
	 * @param clz
	 * @throws Exception
	 */
	private static void checkElement(Element element, Object jsonName, 
			StringBuffer E4003, StringBuffer E4004, 
			Type type, Class clz, int pos) throws Exception{
		if(jsonName!=null&&"BmsInfo".equals(element.attributeValue("name"))){
			JSONObject jsonObject = (JSONObject) JSON.toJSON(jsonName);
			checkData(jsonObject, element, E4003, E4004,type, clz,-1);
		}else if(jsonName == null &&"no".equals(element.attributeValue("isNull"))){
			E4003.append(element.attributeValue("name")+"["+element.attributeValue("value")+"];");
		    return;
		}else if("".equals(jsonName) &&"no".equals(element.attributeValue("isNull"))){
			E4003.append(element.attributeValue("name")+"["+element.attributeValue("value")+"];");
			return;
		}else if(jsonName != null){
			
			String checkType = "_"+element.attributeValue("type");
			int size = -1;
			int maxsize = -1;
			if(checkType.equals("_String")){
				if(element.attributeValue("length") != null){
					size = Integer.parseInt(element.attributeValue("length"));
				}
				if(element.attributeValue("maxlength")!=null){
					maxsize = Integer.parseInt(element.attributeValue("maxlength"));
				}
			}
			String value = element.attributeValue("value");
			String isNull = element.attributeValue("isNull");
			String format = getFormat(element,checkType);
			Map<String,String> enum2Map = getEnumMap(element,checkType);
			Method m = clz.getDeclaredMethod(checkType, String.class,String.class,Object.class,String.class,int.class,HashMap.class,String.class,int.class,int.class);
			E4004.append(m.invoke(type,isNull,element.attributeValue("name"),jsonName,value,pos,enum2Map,format,size, maxsize));
			
		}
	}
	
	public synchronized static Element processJob() {
		logger.info("processJob work begin!");
		logger.info("读取interface.xml文件");
		SAXReader sax = new SAXReader();
		Document document;
		try {
			document = sax.read(Protocol2Parse.class.getResourceAsStream("/conf/V1.0/interface.xml"));
			rootEle = document.getRootElement();
		} catch (DocumentException e) {
			logger.error("processJob异常信息:"+e);
		}
		return rootEle;
	}
	
	public static void main(String[] args) throws Exception{
						
		String Context = Util.ReadFile("json/notification/V1.0/notification_stationInfo.json");
		//System.out.println(validate2Parameter("V1.0","StationInfo",Context));
		
		
	}	
	
}
