package me.retrodaredevil.action.node.environment;

import me.retrodaredevil.action.node.ActionNode;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class VariableEnvironment {
	private final Map<String, ActionNode> declaredActionMap = new HashMap<>();
	private final Map<String, LockSet> lockSetMap = new HashMap<>();
	private final VariableEnvironment outerVariableEnvironment;
	/** The global lockset, which is only initialized when there is no outer variable environment*/
	private final LockSet globalLockSet;

	public VariableEnvironment() {
		this(null);
	}

	public VariableEnvironment(VariableEnvironment outerVariableEnvironment) {
		this.outerVariableEnvironment = outerVariableEnvironment;
		if (outerVariableEnvironment == null) {
			globalLockSet = new LockSet();
		} else {
			globalLockSet = null;
		}
	}

	public ActionNode getDeclaredAction(String name) {
		if (!declaredActionMap.containsKey(name)) {
			if (outerVariableEnvironment == null) {
				throw new NoSuchElementException("Action with name='" + name + "' not declared!");
			}
			return outerVariableEnvironment.getDeclaredAction(name);
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
	public LockSet getLockSet(String name) {
		if (!lockSetMap.containsKey(name)) {
			if (outerVariableEnvironment == null) {
				throw new NoSuchElementException("LockSet with name='" + name + "' not declared!");
			}
			return outerVariableEnvironment.getLockSet(name);
		}
		return lockSetMap.get(name);
	}
	public LockSet getGlobalLockSet() {
		if (outerVariableEnvironment == null) {
			return requireNonNull(globalLockSet);
		}
		return outerVariableEnvironment.getGlobalLockSet();
	}

	public static final class LockSet {
		private final Set<String> lockSet = new HashSet<>();

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
}
