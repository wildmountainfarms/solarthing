package me.retrodaredevil.action.node.environment;

public class ActionEnvironment {
	private final VariableEnvironment variableEnvironment;
	private final InjectEnvironment injectEnvironment;

	public ActionEnvironment(VariableEnvironment variableEnvironment, InjectEnvironment injectEnvironment) {
		this.variableEnvironment = variableEnvironment;
		this.injectEnvironment = injectEnvironment;
	}


	/**
	 * Note: {@link VariableEnvironment}s are not thread safe and are not designed to be used by multiple threads
	 * @return The local variable environment
	 */
	public VariableEnvironment getVariableEnvironment() {
		return variableEnvironment;
	}

	/**
	 * @return The InjectEnvironment. This object is immutable, making it thread safe. Data nested in objects returned be {@link InjectEnvironment#get(Class)} may or may not be thread safe.
	 */
	public InjectEnvironment getInjectEnvironment() {
		return injectEnvironment;
	}

	public ActionEnvironment withVariableEnvironment(VariableEnvironment newVariableEnvironment) {
		return new ActionEnvironment(newVariableEnvironment, injectEnvironment);
	}
	public ActionEnvironment withInjectEnvironment(InjectEnvironment newInjectEnvironment) {
		return new ActionEnvironment(variableEnvironment, newInjectEnvironment);
	}
}
