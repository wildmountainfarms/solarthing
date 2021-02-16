package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public final class Constants {
	private Constants(){ throw new UnsupportedOperationException(); }

	public static final String DATABASE_UPLOAD_ID = "packet_upload";
	public static final String DATABASE_UPLOAD_EVENT_ID = "packet_event_upload";
	public static final String DATABASE_COMMAND_DOWNLOAD_ID = "command_download";

}
