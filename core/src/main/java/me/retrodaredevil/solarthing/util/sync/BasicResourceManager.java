package me.retrodaredevil.solarthing.util.sync;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

/**
 * A resource manager that does nothing to synchronize access or updates to the resource
 */
@NullMarked
public class BasicResourceManager<RESOURCE> implements ResourceManager<RESOURCE> {

	private final RESOURCE resource;

	public BasicResourceManager(RESOURCE resource) {
		this.resource = resource;
	}

	@Override
	public <RESULT extends @Nullable Object> RESULT access(Function<RESOURCE, RESULT> function) {
		return function.apply(resource);
	}

	@Override
	public <RESULT extends @Nullable Object> RESULT update(Function<RESOURCE, RESULT> function) {
		return access(function);
	}

}
