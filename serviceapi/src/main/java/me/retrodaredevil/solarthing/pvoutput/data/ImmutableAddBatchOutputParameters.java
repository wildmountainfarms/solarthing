package me.retrodaredevil.solarthing.pvoutput.data;

import java.util.List;

public class ImmutableAddBatchOutputParameters implements AddBatchOutputParameters {
	private final List<AddOutputParameters> outputs;

	public ImmutableAddBatchOutputParameters(List<AddOutputParameters> outputs) {
		this.outputs = outputs;
	}

	@Override
	public List<AddOutputParameters> getOutputs() {
		return outputs;
	}
}
