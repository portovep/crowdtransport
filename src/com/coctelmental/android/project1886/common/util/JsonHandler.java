package com.coctelmental.android.project1886.common.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;

public class JsonHandler {


	public static String toJson(Object object) {
		Gson gson = new Gson();
		return gson.toJson(object);
	}
	
	public static String toJson(Object object, Type type) {
		Gson gson = new Gson();
		return gson.toJson(object, type);
	}

	public static <T> T fromJson(String jsonObject, Type type) {
		Gson gson = new Gson();
		return gson.fromJson(jsonObject, type);
	}
	
	public static <T> T fromJson(String jsonObject, Class<T> classType) {
		Gson gson = new Gson();
		return gson.fromJson(jsonObject, classType);
	}

}
