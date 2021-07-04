package me.retrodaredevil.solarthing.pvoutput.data;

import java.util.List;

@Deprecated
public class ImmutableDeprecatedAddBatchOutputParameters implements DeprecatedAddBatchOutputParameters {
	private final List<AddOutputParameters> outputs;

	public ImmutableDeprecatedAddBatchOutputParameters(List<AddOutputParameters> outputs) {
		this.outputs = outputs;
	}

	@Override
	public List<AddOutputParameters> getOutputs() {
		return outputs;
	}
}
