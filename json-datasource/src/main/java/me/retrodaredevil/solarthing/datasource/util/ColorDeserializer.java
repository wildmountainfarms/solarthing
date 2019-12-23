package me.retrodaredevil.solarthing.datasource.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.awt.*;
import java.io.IOException;

public class ColorDeserializer extends JsonDeserializer<Color> {
	@Override
	public Color deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
		String string = p.getValueAsString();
		String valueString = string.substring(5);
		valueString = valueString.substring(0, valueString.length() - 1);
		String[] split = valueString.split(", ");
		int r = Integer.parseInt(split[0]);
		int g = Integer.parseInt(split[1]);
		int b = Integer.parseInt(split[2]);
		int a = Integer.parseInt(split[3]);
		return new Color(r, g, b, a);
	}
}
