package me.retrodaredevil.solarthing.util.sync;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public class ReadWriteResourceManager<RESOURCE> implements ResourceManager<RESOURCE> {
	private final RESOURCE resource;

	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	public ReadWriteResourceManager(RESOURCE resource) {
		this.resource = resource;
	}

	@Override
	public <RESULT> RESULT access(Function<RESOURCE, RESULT> function) {
		readWriteLock.readLock().lock();
		try {
			return function.apply(resource);
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	@Override
	public <RESULT> RESULT update(Function<RESOURCE, RESULT> function) {
		readWriteLock.writeLock().lock();
		try {
			return function.apply(resource);
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}
}
