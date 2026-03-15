package me.retrodaredevil.solarthing.program.subprogram.analyze;

import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record DataChunk(
		List<StoredPacketGroup> statusPackets,
		List<StoredPacketGroup> eventPackets
) {}
