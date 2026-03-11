package me.retrodaredevil.solarthing.config.options;


import me.retrodaredevil.solarthing.config.request.DataRequester;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;

public interface PacketHandlingOption extends TimeZoneOption {

	@Deprecated
	@NonNull List<Path> getDatabaseConfigurationFilePaths();

	@NonNull DatabaseConfigSettings getDatabaseConfigSettings();

	@NonNull String getSourceId();
	int getFragmentId();

	@Nullable Integer getUniqueIdsInOneHour();
	boolean isDocumentIdShort();

	@NonNull List<DataRequester> getDataRequesterList();
}
