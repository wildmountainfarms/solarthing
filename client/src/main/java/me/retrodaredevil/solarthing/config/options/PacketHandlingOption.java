package me.retrodaredevil.solarthing.config.options;


import java.io.File;
import java.util.List;

public interface PacketHandlingOption {

	List<File> getDatabaseConfigurationFiles();

	String getSourceId();
	Integer getFragmentId();

	Integer getUniqueIdsInOneHour();

	List<ExtraOptionFlag> getExtraOptionFlags();
}
