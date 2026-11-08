package me.retrodaredevil.solarthing.rest.graphql.service.web;

import me.retrodaredevil.solarthing.util.JacksonUtil;
import okhttp3.Cookie;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class DatabaseAuthorizationTest {

	@Test
	void test() {
		ObjectMapper objectMapper = JacksonUtil.defaultMapper();
		String output = objectMapper.writeValueAsString(DatabaseAuthorization.create(
				new Cookie.Builder()
						.name("AuthSession")
						.value("mybase64datahere")
						.expiresAt(System.currentTimeMillis() + 5000)
						.hostOnlyDomain("localhost")
						.path("/")
						.build()
		));
		System.out.println(output);
	}

	@Test
	void testCreate() {
		DatabaseAuthorization.create("db.example.com", "AuthSession=mysecrethere; expires=Tue, 07 Jun 2022 04:11:52 GMT; path=/; httponly");
	}
}
