package me.retrodaredevil.solarthing.config.options;


import me.retrodaredevil.solarthing.config.request.DataRequester;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;

@NullMarked
public interface PacketHandlingOption extends TimeZoneOption {

	@Deprecated
	List<Path> getDatabaseConfigurationFilePaths();

	DatabaseConfigSettings getDatabaseConfigSettings();

	String getSourceId();
	int getFragmentId();

	@Nullable Integer getUniqueIdsInOneHour();
	boolean isDocumentIdShort();

	List<DataRequester> getDataRequesterList();
}
