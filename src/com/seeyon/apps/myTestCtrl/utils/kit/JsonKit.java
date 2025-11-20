package com.seeyon.apps.myTestCtrl.utils.kit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.text.SimpleDateFormat;

/**
 * (推荐) 使用 Jackson (com.fasterxml.jackson) 重写的 JsonKit
 */
public class JsonKit {

	// 创建一个可重用的ObjectMapper实例
	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		// 1. 配置日期格式化，实现与FastJSON完全相同的效果
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * 将任何Java对象序列化为JSON字符串，并正确格式化日期。
	 */
	public static String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			// 在实际项目中，这里应该记录日志
			throw new RuntimeException("Failed to serialize object to JSON", e);
		}
	}

	/**
	 * 将JSON字符串解析为 Map (功能上等同于 FastJSON 的 parseObject)
	 */
	public static java.util.Map<String, Object> parse(String text) {
		try {
			// Jackson 将其解析为通用的 Map
			return objectMapper.readValue(text, java.util.Map.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse JSON string to Map", e);
		}
	}

	/**
	 * 将JSON字符串反序列化为指定的Java POJO对象 (完美实现)
	 */
	public static <T> T parse(String text, Class<T> clazz) {
		try {
			return objectMapper.readValue(text, clazz);
		} catch (Exception e) {
			throw new RuntimeException("Failed to deserialize JSON to " + clazz.getName(), e);
		}
	}
}