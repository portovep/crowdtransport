package com.coctelmental.android.project1886.common.util;

import com.google.gson.Gson;

public class JsonHandler {


	public static String toJson(Object object) {
		Gson gson = new Gson();
		return gson.toJson(object);
	}


	public static <T> T fromJson(String jsonObject, Class<T> classType) {
		Gson gson = new Gson();
		return gson.fromJson(jsonObject, classType);
	}

}
