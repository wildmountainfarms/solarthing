package me.retrodaredevil.solarthing.rest.graphql;

import me.retrodaredevil.couchdbjava.okhttp.util.OkHttpUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

@NullMarked
public class OkHttpTest {
	@Test
	void test() {
		OkHttpUtil.createJsonRequestBody("{ \"hi\": 3 }");
	}
}
