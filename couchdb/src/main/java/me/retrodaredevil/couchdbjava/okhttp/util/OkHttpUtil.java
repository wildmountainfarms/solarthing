package me.retrodaredevil.couchdbjava.okhttp.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.CouchDbStatusCode;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbUnauthorizedException;
import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import me.retrodaredevil.couchdbjava.response.SessionGetResponse;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

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
			throw new CouchDbException("Problem reading json data!", e);
		}
	}
	public static @Nullable ErrorResponse parseErrorResponse(Response response) {
		try {
			return parseResponseBodyJson(response, ErrorResponse.class);
		} catch (CouchDbException e) {
		}
		return null;
	}
	public static CouchDbException createExceptionFromResponse(Response response) {
		if (response.isSuccessful()) {
			throw new IllegalArgumentException("This response is successful!");
		}
		ErrorResponse error = parseErrorResponse(response);

		switch(response.code()) {
			case CouchDbStatusCode.UNAUTHORIZED:
				return new CouchDbUnauthorizedException("You are unauthorized!", error);
			case CouchDbStatusCode.NOT_FOUND:
				return new CouchDbException("Got 'not found'!");
		}
		return new CouchDbException("Unknown status code! code: " + response.code());
	}
}
