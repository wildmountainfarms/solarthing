package me.retrodaredevil.couchdbjava.okhttp.util;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public final class OkHttpUtil {
	private OkHttpUtil() { throw new UnsupportedOperationException(); }

	public static RequestBody createJsonRequestBody(String json) {
		return RequestBody.create(json, MediaType.parse("application/json; charset=utf-8");
	}
}
