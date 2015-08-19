package api.facebook.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * 通用的json数据解析类
 * @author chenkedi
 *
 */
public class JsonAnalyze
{
	
	/**
	 * 遍历解析json对象中的数组类型，并转换为List<Map>类型
	 * @param jsonArray
	 * @param keys 需要提取的键
	 * @return
	 */
	public List<Map<String,String>> jsonArrayAnalyze(JSONArray jsonArray,List<String> keys){
		
		Map<String,String> jsonMap=new HashMap<String,String>();
		List<Map<String,String>> jsonList= new ArrayList<Map<String,String>>();
		
		for(int i=0;i<jsonArray.length();i++){
			JSONObject tempJsonObj=jsonArray.getJSONObject(i);
			for(String key : keys){
				jsonMap.put(key, tempJsonObj.getString(key));
			}
			jsonList.add(jsonMap);
		}
		return jsonList;
	}
	
	
	/**
	 * 解析单层的json对象，转换为Map类型
	 * @param jsonObject
	 * @param keys
	 * @return
	 */
	public Map<String,String> jsonLeafObjectAnalyze(JSONObject jsonObject,List<String> keys){
		
		Map<String,String> jsonMap=new HashMap<String,String>();
		
		for(String key : keys){
			jsonMap.put(key, jsonObject.getString(key));
		}	
		return jsonMap;
	}
	
	
	/**
	 * 检查Facebook返回值是否是包含错误信息的json
	 * @param jsonObject
	 * @return
	 */
	public boolean isErrorJson(JSONObject jsonObject){
		if(jsonObject.has("error")){
			
			return true;
		}else{
			
			return false;
		}
	}
	
	
	/**
	 * 获取错误信息以便判断异常类型
	 * @param jsonObject
	 * @return
	 */
	public Map<String,String> jsonErrorMessage(JSONObject jsonObject){
		
		Map<String,String> jsonMap=new HashMap<String,String>();
		
		jsonObject=jsonObject.getJSONObject("error");
		jsonMap.put("message", jsonObject.getString("message"));
		jsonMap.put("code", String.valueOf(jsonObject.getInt("code")));
		
		return jsonMap;
		
	}
	
}
