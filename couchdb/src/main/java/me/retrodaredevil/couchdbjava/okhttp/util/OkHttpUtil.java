package me.retrodaredevil.couchdbjava.okhttp.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.response.SessionGetResponse;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public final class OkHttpUtil {
	private OkHttpUtil() { throw new UnsupportedOperationException(); }

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static RequestBody createJsonRequestBody(String json) {
		return RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
	}

	public static <T> T parseResponseBodyJson(Response response, Class<T> clazz) throws CouchDbException {
		try {
			return MAPPER.readValue(requireNonNull(response.body()).byteStream(), clazz);
		} catch (JsonMappingException | JsonParseException e) {
			throw new CouchDbException("Received bad json data!", e);
		} catch (IOException e) {
			throw new RuntimeException("We shouldn't have a problem reading the body!", e);
		}
	}
}
