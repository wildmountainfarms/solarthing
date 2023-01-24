package me.retrodaredevil.action.node.environment;

import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.expression.Expression;
import me.retrodaredevil.action.node.expression.type.ExpressionType;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class VariableEnvironment {
	private final Map<String, DeclaredAction> declaredActionMap = new HashMap<>();
	private final Map<String, LockSet> lockSetMap = new HashMap<>();
	/** A map of variable name to variable. Note null values are allowed, and represent a variable declared, but not initialized*/
	private final Map<String, Variable> variableMap = new HashMap<>();
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

	public DeclaredAction getDeclaredAction(String name) {
		if (!declaredActionMap.containsKey(name)) {
			if (outerVariableEnvironment == null) {
				throw new NoSuchElementException("Action with name='" + name + "' not declared!");
			}
			return outerVariableEnvironment.getDeclaredAction(name);
		}
		return declaredActionMap.get(name);
	}
	public void setDeclaredAction(String name, ActionEnvironment actionEnvironment, ActionNode actionNode) {
		requireNonNull(name); requireNonNull(actionEnvironment); requireNonNull(actionNode);

		if (declaredActionMap.containsKey(name)) {
			throw new IllegalStateException("Action with name='" + name + "' already declared!");
		}
		declaredActionMap.put(name, new DeclaredAction(actionEnvironment, actionNode));
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

	public Expression referenceVariable(String name) {
		if (!variableMap.containsKey(name)) {
			if (outerVariableEnvironment == null) {
				throw new NoSuchElementException("Variable with name='" + name + "' not declared!");
			}
			return outerVariableEnvironment.referenceVariable(name);
		}
		Variable variable = variableMap.get(name);
		if (variable == null) { // null value present in map is a special case
			throw new IllegalStateException("Cannot reference uninitialized variable! name='" + name + "'");
		}
		return variable.getExpression();
	}

	/**
	 * If {@code expression} is null, the variable cannot be referenced until it is initialized by a set
	 * @param name The name of the variable
	 * @param expression The expression or null
	 */
	public void initializeVariable(String name, Expression expression) {
		if (variableMap.containsKey(name)) {
			throw new IllegalStateException("Variable with name='" + name + "' already exists in this scope!");
		}
		variableMap.put(name, expression == null ? null : new Variable(expression));
	}
	public void assignVariable(String name, Expression expression) {
		if (!variableMap.containsKey(name)) {
			if (outerVariableEnvironment == null) {
				throw new NoSuchElementException("Variable with name='" + name + "' not declared!");
			}
			outerVariableEnvironment.assignVariable(name, expression);
			return;
		}
		Variable existingVariable = variableMap.get(name);
		if (existingVariable == null) {
			variableMap.put(name, new Variable(expression));
		} else {
			existingVariable.setExpression(expression);
		}
	}

	public static final class DeclaredAction {
		/** The action environment the definition of this action was in */
		private final ActionEnvironment actionEnvironment;
		private final ActionNode actionNode;

		public DeclaredAction(ActionEnvironment actionEnvironment, ActionNode actionNode) {
			this.actionEnvironment = requireNonNull(actionEnvironment);
			this.actionNode = requireNonNull(actionNode);
		}

		public ActionEnvironment getActionEnvironment() {
			return actionEnvironment;
		}

		public ActionNode getActionNode() {
			return actionNode;
		}

		public Action createActionWithOriginalScope() {
			return actionNode.createAction(actionEnvironment);
		}
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
	public static final class Variable {
		private final ExpressionType type;
		private Expression expression;

		public Variable(Expression expression) {
			requireNonNull(expression);
			this.type = expression.getType();
			this.expression = expression;
		}

		public ExpressionType getType() {
			return type;
		}

		public Expression getExpression() {
			return expression;
		}

		public void setExpression(Expression expression) {
			this.expression = requireNonNull(expression);
		}
	}
}
