package com.sunrise.jsonparser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonToMap {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map toMap(JSONObject jsonObject) throws JSONException {

		Map result = new HashMap();
		Iterator iterator = jsonObject.keys();
		String key = null;
		String value = null;

		while (iterator.hasNext()) {

			key = (String) iterator.next();
			value = jsonObject.getString(key);
			result.put(key, value);
		}

		return result;
	}
}