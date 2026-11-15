package me.retrodaredevil.solarthing.jackson;

import org.jspecify.annotations.NullMarked;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ValueDeserializer;

@Deprecated
@NullMarked
public abstract class UnwrappedDeserializer<T, U> extends ValueDeserializer<T> {
	// This aims to be a fix for https://github.com/FasterXML/jackson-databind/issues/650
	// TODO remove this class

	private final Class<U> builderClass;
	private final BuilderFunction<U, T> build;

	protected UnwrappedDeserializer(Class<U> builderClass, BuilderFunction<U, T> build) {
		this.builderClass = builderClass;
		this.build = build;
	}

	@Override
	public T deserialize(JsonParser p, DeserializationContext ctxt) {
		UnwrappedDeserializeHelper helper = new UnwrappedDeserializeHelper(p, ctxt, builderClass);
		U builder = helper.readValue(builderClass);
		if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
			helper.assertNoUnknown();
		}
		return build.build(builder);
	}
	@FunctionalInterface
	public interface BuilderFunction<T, R> {
		R build(T var1);
	}
}
