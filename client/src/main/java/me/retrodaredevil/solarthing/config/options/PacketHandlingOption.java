package me.retrodaredevil.solarthing.config.options;


import me.retrodaredevil.solarthing.config.request.DataRequester;

import java.io.File;
import java.util.List;

public interface PacketHandlingOption extends TimeZoneOption {

	List<File> getDatabaseConfigurationFiles();

	String getSourceId();
	int getFragmentId();

	Integer getUniqueIdsInOneHour();

	List<ExtraOptionFlag> getExtraOptionFlags();
	List<DataRequester> getDataRequesterList();
}
