package me.retrodaredevil.solarthing.util.sync;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ResourceManager<RESOURCE>{
	<RESULT> RESULT access(Function<RESOURCE, RESULT> function);

	<RESULT> RESULT update(Function<RESOURCE, RESULT> function);

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
