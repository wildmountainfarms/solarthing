package me.retrodaredevil.couchdbjava.okhttp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.util.HeaderRequestInterceptor;
import me.retrodaredevil.couchdbjava.okhttp.util.OkHttpUtil;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class OkHttpCouchDbInstance implements CouchDbInstance {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	final OkHttpClient client;
	final HttpUrl url;
	final CouchDbAuth couchDbAuth;

	public OkHttpCouchDbInstance(OkHttpClient client, HttpUrl url, CouchDbAuth couchDbAuth) {
		this.client = client.newBuilder()
				.addInterceptor(new HeaderRequestInterceptor("Accept", "application/json"))
				.build();
		this.url = url;
		this.couchDbAuth = couchDbAuth;
	}
	HttpUrl.Builder createUrlBuilder() {
		return new HttpUrl.Builder()
				.scheme(url.scheme())
				.host(url.host())
				.port(url.port())
				.query(url.query())
				;
	}
	Response executeCall(Call call) throws CouchDbException {
		try {
			return call.execute();
		} catch (IOException e) {
			throw new CouchDbException(e);
		}
	}

	@Override
	public CouchDbDatabase getDatabase(String path) {
		return null;
	}

	@Override
	public CouchDbDatabase getReplicatorDatabase() {
		return null;
	}

	@Override
	public CouchDbDatabase getUsersDatabase() {
		return null;
	}

	@Override
	public String authSession(String username, String password) throws CouchDbException {
		requireNonNull(username);
		requireNonNull(password);
		HttpUrl url = createUrlBuilder().addPathSegment("_session").build();
		Map<String, String> map = new HashMap<>();
		map.put("name", username);
		map.put("password", password);
		String jsonData;
		try {
			jsonData = MAPPER.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		Request request = new Request.Builder()
				.url(url)
				.post(OkHttpUtil.createJsonRequestBody(jsonData))
				.build();
		Call call = client.newCall(request);
		Response response = executeCall(call);
		if (response.isSuccessful()) {
			List<Cookie> cookies = Cookie.parseAll(url, response.headers());
			String authSession = null;
			for (Cookie c : cookies) {
				if ("AuthSession".equals(c.name())) {
					authSession = c.value();
					break;
				}
			}
			if (authSession == null) {
				throw new CouchDbException("No AuthSession cookie was set!");
			}
			return authSession;
		}
	}
}
