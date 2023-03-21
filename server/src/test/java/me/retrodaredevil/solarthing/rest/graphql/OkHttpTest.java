package me.retrodaredevil.solarthing.rest.graphql;

import me.retrodaredevil.couchdbjava.okhttp.util.OkHttpUtil;
import org.junit.jupiter.api.Test;

public class OkHttpTest {
	@Test
	void test() {
		OkHttpUtil.createJsonRequestBody("{ \"hi\": 3 }");
	}
}
