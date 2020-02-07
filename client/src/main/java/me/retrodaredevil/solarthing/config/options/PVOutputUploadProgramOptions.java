package me.retrodaredevil.solarthing.config.options;

public class PVOutputUploadProgramOptions implements ProgramOptions {
	@Override
	public ProgramType getProgramType() {
		return ProgramType.PV_OUTPUT_UPLOAD;
	}
}
