package me.retrodaredevil.solarthing.rest.integration;

import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource("/test.properties")
@Import(GraphQLHttpJacksonTest.MapperConfiguration.class)
@NullMarked
class GraphQLHttpJacksonTest {
	@Autowired
	private WebApplicationContext context;

	@Test
	void httpRequestsUseTheConfiguredMapper() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		// This nonstandard JSON is accepted only by the customized mapper below.
		MvcResult result = mvc.perform(post("/graphql")
				.contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
				.content("{'query':'{ __typename }'}")).andReturn();
		if (result.getRequest().isAsyncStarted()) {
			mvc.perform(asyncDispatch(result)).andExpect(status().isOk())
					.andExpect(content().json("{\"data\":{\"__typename\":\"Query\"}}"));
		} else {
			status().isOk().match(result);
			content().json("{\"data\":{\"__typename\":\"Query\"}}").match(result);
		}
	}

	@TestConfiguration(proxyBeanMethods = false)
	static class MapperConfiguration {
		@Bean
		@Primary
		JsonMapper customizedMapper() {
			return JacksonUtil.defaultMapperBuilder()
					.enable(JsonReadFeature.ALLOW_SINGLE_QUOTES).build();
		}
	}
}
