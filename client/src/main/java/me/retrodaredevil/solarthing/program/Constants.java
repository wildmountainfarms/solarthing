package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public final class Constants {
	private Constants(){ throw new UnsupportedOperationException(); }

	@Deprecated
	public static final String DATABASE_UPLOAD_ID = "packet_upload";
	@Deprecated
	public static final String DATABASE_COMMAND_DOWNLOAD_ID = "command_download";

}
