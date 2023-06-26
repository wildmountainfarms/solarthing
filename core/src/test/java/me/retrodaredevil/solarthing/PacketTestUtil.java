package me.retrodaredevil.solarthing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.util.JacksonUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PacketTestUtil {
	private PacketTestUtil() { throw new UnsupportedOperationException(); }

	public static final Path SOLARTHING_ROOT = Paths.get("..");

	public static <T> void testJson(T originalPacket, Class<T> packetClass) throws JsonProcessingException {
		testJson(originalPacket, packetClass, false);
	}
	public static <T> void testJson(T originalPacket, Class<T> packetClass, boolean isEqualsImplemented) throws JsonProcessingException {
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		String json = mapper.writeValueAsString(originalPacket);
//		System.out.println(json);
		T output = mapper.readValue(json, packetClass);
		String json2 = mapper.writeValueAsString(output);
		assertEquals(json, json2);
		if (isEqualsImplemented) {
			assertEquals(originalPacket, output, "equals() comparison failed for originalPacket=" + originalPacket);
		}
	}
	public static <T extends Packet> void testDirectory(Path directory, Class<T> readUsingClass, Function<? super T, ? extends Boolean> requirement) {
		assertTrue(Files.isDirectory(directory));

		ObjectMapper mapper = JacksonUtil.defaultMapper();

		try (Stream<Path> files = Files.list(directory)) {
			files.forEach(file -> {
				T packet;
				try {
					packet = mapper.readValue(Files.newInputStream(file), readUsingClass);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				assertTrue(requirement.apply(packet));
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
