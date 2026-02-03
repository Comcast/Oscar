package com.comcast.oscar.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @bannerLicense
	Copyright 2015 Comcast Cable Communications Management, LLC<br>
	___________________________________________________________________<br>
	Licensed under the Apache License, Version 2.0 (the "License")<br>
	you may not use this file except in compliance with the License.<br>
	You may obtain a copy of the License at<br>
	http://www.apache.org/licenses/LICENSE-2.0<br>
	Unless required by applicable law or agreed to in writing, software<br>
	distributed under the License is distributed on an "AS IS" BASIS,<br>
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>
	See the License for the specific language governing permissions and<br>
	limitations under the License.<br>


 * @author Maurice Garcia (mgarcia01752@outlook.com)
 */
public class JSONTools {

	public final static Integer DATA_TYPE_STRING 	= 0;
	public final static Integer DATA_TYPE_INTEGER 	= 1;
	public final static Integer DATA_TYPE_BOOLEAN 	= 2;
	public final static Integer DATA_TYPE_LONG 		= 3;
	public final static Integer DATA_TYPE_DOUBLE 	= 4;
	
	/**
	 * 
	 * @param jo
	 * @param sKey
	
	 * @return boolean
	 */
	public static boolean containJSONArray (JSONObject jo , String sKey) {
		
		boolean boolContainJSON = false;
		
		try {
			if (jo.getString(sKey).toString().substring(0).contains("[")) 
				boolContainJSON =  true;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return boolContainJSON;
	}
	
	/**
	 * 
	 * @param object
	
	
	 * @return Object
	 * @throws JSONException */
    @SuppressWarnings("rawtypes")
	public static Object toJSON(Object object) throws JSONException {
        
    	if (object instanceof Map) {
    		
            JSONObject json = new JSONObject();
            
            Map map = (Map) object;
            
            for (Object key : map.keySet()) {
                json.put(key.toString(), toJSON(map.get(key)));
            }
            
            return json;
        
    	} else if (object instanceof Iterable) {
            
    		JSONArray ja = new JSONArray();
            
    		for (Object value : ((Iterable)object)) {
                ja.put(value);
            }
            
    		return ja;
        
    	} else {
            return object;
        }
    }
 
    /**
     * 
     * @param jo
    
     * @return boolean
     */
    public static boolean isEmptyObject(JSONObject jo) {
        return jo.names() == null;
    }
 
    /**
     * 
     * @param jo
     * @param key
    
    
     * @return Map<String,Object>
     * @throws JSONException */
    public static Map<String, Object> getMap(JSONObject jo, String key) throws JSONException {
        return toMap(jo.getJSONObject(key));
    }
 
    /**
     * 
     * @param jo
    
    
     * @return Map<String,Object>
     * @throws JSONException */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> toMap(JSONObject jo) throws JSONException {
        
		Map<String, Object> map = new HashMap();
        
		Iterator keys = jo.keys();
        
		while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(jo.get(key)));
        }
        
		return map;
    }
 
	/**
	 * 
	 * @param ja
	
	
	 * @return List
	 * @throws JSONException */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static List toList(JSONArray ja) throws JSONException {
        
    	List list = new ArrayList();
        
    	for (int i = 0; i < ja.length(); i++) {
            list.add(fromJson(ja.get(i)));
        }
        
    	return list;
    }
 
    /**
     * 
     * @param jo
     * @param sString1
     * @param sString2
     * @param iJSONDataType
    
     * @return Map<Object,Object>
     */
    public static Map<Object,Object> mapObjectToObject (JSONObject jo , String sString1 , String sString2 , int iJSONDataType) {
    	
    	Map<Object,Object> mooTypeByteLength = new HashMap<Object,Object>();
    	   	
    	try {
    		
    		if (iJSONDataType == DATA_TYPE_STRING) {
    			mooTypeByteLength.put(jo.getString(sString1), jo.getString(sString2));	
    		} else if (iJSONDataType == DATA_TYPE_INTEGER) {
    			mooTypeByteLength.put(jo.getInt(sString1), jo.getInt(sString2));
    		} else if (iJSONDataType == DATA_TYPE_BOOLEAN) {
    			mooTypeByteLength.put(jo.getBoolean(sString1), jo.getBoolean(sString2));
    		} else if (iJSONDataType == DATA_TYPE_LONG) {
    			mooTypeByteLength.put(jo.getLong(sString1), jo.getLong(sString2));
    		} else if (iJSONDataType == DATA_TYPE_DOUBLE) {
    			mooTypeByteLength.put(jo.getDouble(sString1), jo.getDouble(sString2));
    		}
    		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return mooTypeByteLength;
    }
    
    /**
     * 
     * @param oj
    
    
     * @return Object
     * @throws JSONException */
    private static Object fromJson(Object oj) throws JSONException {
        
    	if (oj == JSONObject.NULL) {
        
    		return null;
        
    	} else if (oj instanceof JSONObject) {
            
    		return toMap((JSONObject) oj);
        
    	} else if (oj instanceof JSONArray) {
            
    		return toList((JSONArray) oj);
        
    	} else {
            
    		return oj;
        
    	}
    }

}
