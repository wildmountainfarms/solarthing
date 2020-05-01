package me.retrodaredevil.solarthing.actions;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class ActionEnvironment {
	private final Map<String, ActionNode> declaredActionMap = new HashMap<>();
	private final Set<String> lockSet = new HashSet<>();
	private String error;

	public void setError(String error) {
		this.error = error;
	}
	public String getError() {
		return error;
	}
	public ActionNode getDeclaredAction(String name) {
		if (!declaredActionMap.containsKey(name)) {
			throw new NoSuchElementException("Action with name='" + name + "' not declared!");
		}
		return declaredActionMap.get(name);
	}
	public void setDeclaredAction(String name, ActionNode action) {
		requireNonNull(name); requireNonNull(action);

		if (declaredActionMap.containsKey(name)) {
			throw new IllegalStateException("Action with name='" + name + "' already declared!");
		}
		declaredActionMap.put(name, action);
	}
	public boolean isLocked(String lockName) {
		return lockSet.contains(lockName);
	}
	public void lock(String lockName) {
		lockSet.add(lockName);
	}
	public void unlock(String lockName) {
		lockSet.remove(lockName);
	}
}
