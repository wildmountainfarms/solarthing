package me.retrodaredevil.solarthing.pvoutput.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.pvoutput.CsvUtil;

import java.util.List;

@JsonExplicit
public interface AddBatchOutputParameters {
	int MAX_BATCH_SIZE = 100;
	List<AddOutputParameters> getOutputs();

	@JsonProperty("data")
	default String getCsvDataString() {
		StringBuilder r = new StringBuilder();
		boolean first = true;
		for (AddOutputParameters addOutputParameters : getOutputs()) {
			if (!first) {
				r.append(';');
			}
			r.append(CsvUtil.toCsvString(addOutputParameters.toCsvArray()));
			first = false;
		}
		return r.toString();
	}
}
