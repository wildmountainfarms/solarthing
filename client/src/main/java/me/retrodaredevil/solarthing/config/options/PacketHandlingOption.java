package me.retrodaredevil.solarthing.config.options;


import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.config.request.DataRequester;

import java.io.File;
import java.util.List;

public interface PacketHandlingOption extends TimeZoneOption {

	@NotNull List<File> getDatabaseConfigurationFiles();

	@NotNull String getSourceId();
	int getFragmentId();

	Integer getUniqueIdsInOneHour();
	boolean isDocumentIdShort();

	@NotNull List<DataRequester> getDataRequesterList();
}
