package me.retrodaredevil.solarthing.pvoutput;

import me.retrodaredevil.solarthing.pvoutput.data.AddOutputParameters;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParameters;

public interface PVOutputExporter {
	void exportOutput(AddOutputParameters parameters) throws PVOutputException;
	void exportStatus(AddStatusParameters parameters) throws PVOutputException;
}
