package me.retrodaredevil.solarthing.actions.environment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class InjectEnvironment {
	private final Map<Class<?>, Object> map;

	public InjectEnvironment(Map<Class<?>, Object> map) {
		this.map = Collections.unmodifiableMap(new HashMap<>(map));
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz) {
		T r = (T) map.get(clazz);
		if (r == null) {
			throw new NoSuchElementException("There was no element with the class: " + clazz);
		}
		return r;
	}

	public static final class Builder {
		private final Map<Class<?>, Object> map = new HashMap<>();

		public Builder add(Object object) {
			map.put(object.getClass(), object);
			return this;
		}
		public <T> Builder add(Class<? super T> clazz, T object) {
			map.put(clazz, object);
			return this;
		}
		public InjectEnvironment build() {
			return new InjectEnvironment(map);
		}
	}

}
