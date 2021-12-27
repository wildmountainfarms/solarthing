package me.retrodaredevil.solarthing.util.sync;

import java.util.function.Function;

public class SynchronizedResourceManager<RESOURCE> implements ResourceManager<RESOURCE> {
	private final Object mutex = new Object();
	private final RESOURCE resource;

	public SynchronizedResourceManager(RESOURCE resource) {
		this.resource = resource;
	}

	@Override
	public <RESULT> RESULT access(Function<RESOURCE, RESULT> function) {
		synchronized (mutex) {
			return function.apply(resource);
		}
	}

	@Override
	public <RESULT> RESULT update(Function<RESOURCE, RESULT> function) {
		return access(function);
	}
}
