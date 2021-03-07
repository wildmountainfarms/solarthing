package me.retrodaredevil.solarthing.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public abstract class UnwrappedDeserializer<T, U> extends JsonDeserializer<T> {
	// This aims to be a fix for https://github.com/FasterXML/jackson-databind/issues/650

	private final Class<U> builderClass;
	private final BuilderFunction<U, T> build;

	protected UnwrappedDeserializer(Class<U> builderClass, BuilderFunction<U, T> build) {
		this.builderClass = builderClass;
		this.build = build;
	}

	@Override
	public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		UnwrappedDeserializeHelper helper = new UnwrappedDeserializeHelper(p, builderClass);
		U builder = helper.readValue(builderClass);
		helper.assertNoUnknown();
		return build.build(builder);
	}
	@FunctionalInterface
	public interface BuilderFunction<T, R> {
		R build(T var1);
	}
}
