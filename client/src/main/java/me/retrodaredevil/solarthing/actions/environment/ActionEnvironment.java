package me.retrodaredevil.solarthing.actions.environment;

public class ActionEnvironment {
	private final VariableEnvironment globalEnvironment;
	private final VariableEnvironment localEnvironment;
	private final InjectEnvironment injectEnvironment;

	public ActionEnvironment(VariableEnvironment globalEnvironment, VariableEnvironment localEnvironment, InjectEnvironment injectEnvironment) {
		this.globalEnvironment = globalEnvironment;
		this.localEnvironment = localEnvironment;
		this.injectEnvironment = injectEnvironment;
	}

	/**
	 * Note: {@link VariableEnvironment}s are not thread safe and are not designed to be used by multiple threads
	 * @return The global variable environment
	 */
	public VariableEnvironment getGlobalEnvironment() {
		return globalEnvironment;
	}

	/**
	 * Note: {@link VariableEnvironment}s are not thread safe and are not designed to be used by multiple threads
	 * @return The local variable environment
	 */
	public VariableEnvironment getLocalEnvironment() {
		return localEnvironment;
	}

	/**
	 * @return The InjectEnvironment. This object is immutable, making it thread safe. Data nested in objects returned be {@link InjectEnvironment#get(Class)} may or may not be thread safe.
	 */
	public InjectEnvironment getInjectEnvironment() {
		return injectEnvironment;
	}
}
