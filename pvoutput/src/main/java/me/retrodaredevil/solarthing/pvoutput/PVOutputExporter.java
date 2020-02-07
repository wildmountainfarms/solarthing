package me.retrodaredevil.solarthing.pvoutput;

import me.retrodaredevil.solarthing.pvoutput.data.AddOutputParamters;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParameters;

public interface PVOutputExporter {
	void exportOutput(AddOutputParamters parameters) throws PVOutputException;
	void exportStatus(AddStatusParameters parameters) throws PVOutputException;
}
