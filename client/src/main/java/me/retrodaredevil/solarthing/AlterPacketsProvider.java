package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;

import java.util.List;

public interface AlterPacketsProvider {
	@Nullable List<VersionedPacket<StoredAlterPacket>> getPackets();
}
