package me.retrodaredevil.solarthing.actions.environment;

import me.retrodaredevil.solarthing.actions.ActionNode;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class VariableEnvironment {
	private final Map<String, ActionNode> declaredActionMap = new HashMap<>();
	private final Set<String> lockSet = new HashSet<>();

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
