package me.retrodaredevil.solarthing.config.options;


import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.config.request.DataRequester;

import java.nio.file.Path;
import java.util.List;

public interface PacketHandlingOption extends TimeZoneOption {

	@Deprecated
	@NotNull List<Path> getDatabaseConfigurationFilePaths();

	@NotNull DatabaseConfigSettings getDatabaseConfigSettings();

	@NotNull String getSourceId();
	int getFragmentId();

	Integer getUniqueIdsInOneHour();
	boolean isDocumentIdShort();

	@NotNull List<DataRequester> getDataRequesterList();
}
