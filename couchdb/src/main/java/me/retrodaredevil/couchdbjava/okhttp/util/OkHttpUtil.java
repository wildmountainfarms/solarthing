package me.retrodaredevil.couchdbjava.okhttp.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.CouchDbStatusCode;
import me.retrodaredevil.couchdbjava.exception.*;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public final class OkHttpUtil {
	private OkHttpUtil() { throw new UnsupportedOperationException(); }

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static RequestBody createJsonRequestBody(String json) {
		requireNonNull(json);
		return RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
	}
	public static RequestBody createJsonRequestBody(JsonData jsonData) {
		return createJsonRequestBody(jsonData.getJson());
	}

	public static <T> T parseResponseBodyJson(ResponseBody body, Class<T> clazz) throws CouchDbException {
		try {
			return MAPPER.readValue(body.byteStream(), clazz);
		} catch (JsonMappingException | JsonParseException e) {
			throw new CouchDbException("Received bad json data!", e);
		} catch (IOException e) {
			throw new CouchDbException("Problem reading json data!", e);
		}
	}
	public static @Nullable ErrorResponse parseErrorResponse(retrofit2.Response<?> response) {
		try {
			return parseResponseBodyJson(requireNonNull(response.errorBody()), ErrorResponse.class);
		} catch (CouchDbException e) {
		}
		return null;
	}
	public static @Nullable ErrorResponse parseErrorResponse(Response response) {
		try {
			return parseResponseBodyJson(requireNonNull(response.body()), ErrorResponse.class);
		} catch (CouchDbException e) {
		}
		return null;
	}
	public static CouchDbException createExceptionFromResponse(Response response) {
		if (response.isSuccessful()) {
			throw new IllegalArgumentException("This response is successful!");
		}
		ErrorResponse error = parseErrorResponse(response);
		return createException(error, response.code());
	}
	public static CouchDbException createExceptionFromResponse(retrofit2.Response<?> response) {
		if (response.isSuccessful()) {
			throw new IllegalArgumentException("This response is successful!");
		}
		ErrorResponse error = parseErrorResponse(response);

		return createException(error, response.code());
	}
	private static CouchDbException createException(ErrorResponse error, int code) {
		switch(code) {
			case CouchDbStatusCode.NOT_MODIFIED:
				return new CouchDbNotModifiedException("Not modified!", error);
			case CouchDbStatusCode.UNAUTHORIZED:
				return new CouchDbUnauthorizedException("You are unauthorized!", error);
			case CouchDbStatusCode.NOT_FOUND:
				return new CouchDbNotFoundException("Got 'not found'!", error);
			case CouchDbStatusCode.UPDATE_CONFLICT:
				return new CouchDbUpdateConflictException("Update conflict!", error);
		}
		return new CouchDbCodeException("Unknown status code! code: " + code, code, error);
	}
}
