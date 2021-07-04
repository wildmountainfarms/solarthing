package me.retrodaredevil.solarthing.pvoutput.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.pvoutput.CsvUtil;

import java.util.List;

/**
 * @deprecated This was used for the old addbatchoutput.
 */
@Deprecated
@JsonExplicit
public interface DeprecatedAddBatchOutputParameters {
	List<AddOutputParameters> getOutputs();

	@JsonProperty("data")
	default String getCsvDataString() {
		StringBuilder r = new StringBuilder();
		boolean first = true;
		for (AddOutputParameters addOutputParameters : getOutputs()) {
			if (!first) {
				r.append(';');
			}
			r.append(CsvUtil.toCsvString(addOutputParameters.toBatchCsvArray()));
			first = false;
		}
		return r.toString();
	}
}
