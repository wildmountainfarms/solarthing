package me.retrodaredevil.solarthing.util.sync;

import java.util.function.Function;

/**
 * A resource manager that does nothing to synchronize access or updates to the resource
 */
public class BasicResourceManager<RESOURCE> implements ResourceManager<RESOURCE> {

	private final RESOURCE resource;

	public BasicResourceManager(RESOURCE resource) {
		this.resource = resource;
	}

	@Override
	public <RESULT> RESULT access(Function<RESOURCE, RESULT> function) {
		return function.apply(resource);
	}

	@Override
	public <RESULT> RESULT update(Function<RESOURCE, RESULT> function) {
		return access(function);
	}

}
