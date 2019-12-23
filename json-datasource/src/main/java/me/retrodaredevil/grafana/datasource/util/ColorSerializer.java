package me.retrodaredevil.grafana.datasource.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.awt.*;
import java.io.IOException;

public class ColorSerializer extends JsonSerializer<Color> {
	@Override
	public void serialize(Color value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString("rgba(" + value.getRed() + ", " + value.getGreen() + ", " + value.getBlue() + ", " + value.getAlpha());
	}
}
