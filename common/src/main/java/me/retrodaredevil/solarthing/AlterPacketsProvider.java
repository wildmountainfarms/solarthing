package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface AlterPacketsProvider {
	/**
	 * Note that you can assume that the returned packets only apply to whatever source ID you are currently using, so you
	 * don't need to check each packet to make sure its source ID is yours
	 * @return The alter packets or null if the packets could not be retrieved.
	 */
	@Nullable List<VersionedPacket<StoredAlterPacket>> getPackets();
}
