package me.retrodaredevil.action.node.environment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Once created, an {@link InjectEnvironment} is immutable and thread safe. Values returned by {@link #get(Class)} should be immutable and thread safe,
 * but data within that data may not be thread safe.
 */
public class InjectEnvironment {
	private final Map<Class<?>, Object> map;

	public InjectEnvironment(Map<Class<?>, Object> map) {
		this.map = Collections.unmodifiableMap(new HashMap<>(map));
	}

	public <T> T getOrNull(Class<T> clazz) {
		return clazz.cast(map.get(clazz));
	}

	public <T> T get(Class<T> clazz) {
		T r = getOrNull(clazz);
		if (r == null) {
			throw new NoSuchElementException("There was no element with the class: " + clazz);
		}
		return r;
	}

	public void require(Class<?> clazz) {
		if (!map.containsKey(clazz)) {
			throw new NoSuchElementException("There was no element with the class: " + clazz);
		}
	}

	public Builder newBuilder() {
		return new Builder(new HashMap<>(map));
	}

	public static final class Builder {
		private final Map<Class<?>, Object> map;

		private Builder(Map<Class<?>, Object> map) {
			this.map = map;
		}
		public Builder() {
			this(new HashMap<>());
		}

		public Builder add(Object object) {
			map.put(object.getClass(), object);
			return this;
		}
		@SuppressWarnings("InconsistentOverloads")
		public <T> Builder add(Class<? super T> clazz, T object) {
			map.put(clazz, object);
			return this;
		}
		public InjectEnvironment build() {
			return new InjectEnvironment(map);
		}
	}

}
