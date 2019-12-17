package me.retrodaredevil.solarthing.pvoutput;

import me.retrodaredevil.solarthing.pvoutput.data.OutputServiceData;
import me.retrodaredevil.solarthing.pvoutput.data.StatusServiceData;

public interface PVOutputExporter {
	void exportOutput(OutputServiceData data) throws PVOutputException;
	void exportStatus(StatusServiceData data) throws PVOutputException;
}
