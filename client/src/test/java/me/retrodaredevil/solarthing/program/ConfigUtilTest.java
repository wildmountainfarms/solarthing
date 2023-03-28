package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigUtilTest {

	@Test
	void testJacksonExceptions() {
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		for (String badJson : new String[] { "", "{" }) {
			try {
				mapper.readValue(badJson, Map.class);
				fail("Reading did not cause exception for: " + badJson);
			} catch (JsonProcessingException e) {
				assertTrue(e.getClass() == MismatchedInputException.class || e.getClass() == JsonEOFException.class, "Failed for: " + badJson);
				assertTrue(e.getMessage().contains("end-of-input"), "Failed for: " + badJson);
			}
		}
	}
	@Test
	void testInterpolator() throws JsonProcessingException {
		// Setting system properties in tests are never a good practice,
		//   but it's OK here because the key of the property being set is only ever used here.
		System.setProperty("test-property-ConfigUtilTest", "asdf");
		assertEquals("hello asdf", ConfigUtil.INTERPOLATOR.replace("hello ${sys:test-property-ConfigUtilTest}"));

		JsonNode jsonNode = new ObjectMapper().readTree("{ \"a\": \"${sys:test-property-ConfigUtilTest}\", \"b\": 2, \"c\": \"My string\" }");
		JsonNode newNode = ConfigUtil.interpolate(jsonNode);
		assertTrue(newNode.isObject());
		assertEquals(3, newNode.size());
		assertEquals("asdf", newNode.get("a").asText());
		assertEquals(2, newNode.get("b").asInt());
		assertEquals("My string", newNode.get("c").asText());
	}
	@Test
	void testRandomRetrieval() {
		// This method isn't exactly for asserting anything, it's just to understand what the INTERPOLATOR is capable of.
		// Remember the working directory is implicitly client/
		final String text = ConfigUtil.INTERPOLATOR.replace("Base64 Decoder:        ${base64Decoder:SGVsbG9Xb3JsZCE=}\n" +
				"Base64 Encoder:        ${base64Encoder:HelloWorld!}\n" +
				"Java Constant:         ${const:java.awt.event.KeyEvent.VK_ESCAPE}\n" +
				"Date:                  ${date:yyyy-MM-dd}\n" +
				"DNS:                   ${dns:address|apache.org}\n" +
				"Environment Variable:  ${env:USERNAME}\n" +
				"File Content:          ${file:UTF-8:../config_templates/io/default_linux_serial.json}\n" +
				"Java:                  ${java:version}\n" +
				"Localhost:             ${localhost:canonical-name}\n" +
				"Script:                ${script:javascript:3 + 4}\n" +
				"System Property:       ${sys:user.dir}\n" +
				"URL Decoder:           ${urlDecoder:Hello%20World%21}\n" +
				"URL Encoder:           ${urlEncoder:Hello World!}\n" +
				"URL Content (HTTP):    ${url:UTF-8:http://www.apache.org}\n" +
				"URL Content (HTTPS):   ${url:UTF-8:https://www.apache.org}\n" +
				"URL Content (File):    ${url:UTF-8:file:///${sys:user.dir}/src/test/resources/document.properties}\n");
		System.out.println(text);
		System.out.println(ConfigUtil.INTERPOLATOR.replace("${urlEncoder:${file:UTF-8:../config_templates/io/default_linux_serial.json}}"));
	}
}
