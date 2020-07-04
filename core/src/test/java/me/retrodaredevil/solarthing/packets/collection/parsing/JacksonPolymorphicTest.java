package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.misc.weather.WeatherPacketType;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Packet;
import org.junit.jupiter.api.Test;

public class JacksonPolymorphicTest {

	@JsonPropertyOrder({"packetType"}) // we want packetType to always be at the top
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "packetType")
	public interface DocumentedPacket extends Packet {
		/**
		 * Should be serialized as "packetType"
		 * @return The packet type
		 */
		@JsonProperty(value = "packetType")
		@NotNull DocumentedPacketType getPacketType();
	}
	public interface TypedDocumentedPacket<T extends Enum<T> & DocumentedPacketType> extends DocumentedPacket {
		@Override
		@NotNull T getPacketType();
	}
	@JsonSubTypes({
			@JsonSubTypes.Type(TemperaturePacket.class)
	})
	public interface WeatherPacket extends TypedDocumentedPacket<WeatherPacketType> {
	}
	@JsonDeserialize(as = CelsiusTemperaturePacket.class)
	@JsonExplicit
	@JsonTypeName("TEMPERATURE")
	public interface TemperaturePacket extends WeatherPacket {
		@Override
		default @NotNull WeatherPacketType getPacketType() {
			return WeatherPacketType.TEMPERATURE;
		}
		@JsonProperty("temperatureCelsius")
		float getTemperatureCelsius();
		@JsonProperty("temperatureFahrenheit")
		float getTemperatureFahrenheit();
	}

	@JsonIgnoreProperties(value = {"temperatureFahrenheit"}, allowGetters = true)
	public static class CelsiusTemperaturePacket implements TemperaturePacket {
		private final float temperatureCelsius;

		@JsonCreator
		public CelsiusTemperaturePacket(
				@JsonProperty(value = "temperatureCelsius", required = true) float temperatureCelsius
		) {
			this.temperatureCelsius = temperatureCelsius;
		}

		@Override
		public float getTemperatureCelsius() {
			return temperatureCelsius;
		}

		@Override
		public float getTemperatureFahrenheit() {
			return temperatureCelsius * 1.8f + 32;
		}
	}

	@Test
	void test() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper().copy();
		objectMapper.getSubtypeResolver().registerSubtypes(WeatherPacket.class);
		String json = "{ \"packetType\": \"TEMPERATURE\", \"temperatureCelsius\": 20.0 }";
		objectMapper.readValue(json, DocumentedPacket.class);
		JsonNode node = objectMapper.readTree(json);
		objectMapper.convertValue(node, DocumentedPacket.class);
	}
}
