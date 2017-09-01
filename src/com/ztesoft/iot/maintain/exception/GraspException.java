package com.ztesoft.iot.maintain.exception;

import com.ztesoft.iot.maintain.cache.BaseCache;

public class GraspException {
	public static String stack(StackTraceElement...stes){
		String result = BaseCache.EMPTY_STRING;
		for(StackTraceElement element : stes){
			result += element + BaseCache.NEXT_LINE;
		}
		return result;
		
	}
}
