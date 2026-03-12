package me.retrodaredevil.solarthing.util.sync;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@NullMarked
public interface ResourceManager<RESOURCE>{
	<RESULT extends @Nullable Object> RESULT access(Function<RESOURCE, RESULT> function);

	<RESULT extends @Nullable Object> RESULT update(Function<RESOURCE, RESULT> function);

	default void access(Consumer<RESOURCE> consumer) {
		access(resource -> {
			consumer.accept(resource);
			return null;
		});
	}
	default void update(Consumer<RESOURCE> consumer) {
		update(resource -> {
			consumer.accept(resource);
			return null;
		});
	}
}
