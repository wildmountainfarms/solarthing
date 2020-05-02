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

	public VariableEnvironment getGlobalEnvironment() {
		return globalEnvironment;
	}

	public VariableEnvironment getLocalEnvironment() {
		return localEnvironment;
	}

	public InjectEnvironment getInjectEnvironment() {
		return injectEnvironment;
	}
}
