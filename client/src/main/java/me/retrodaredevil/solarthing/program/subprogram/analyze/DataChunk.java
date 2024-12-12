package me.retrodaredevil.solarthing.program.subprogram.analyze;

import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;

import java.util.List;

public record DataChunk(
		List<StoredPacketGroup> statusPackets,
		List<StoredPacketGroup> eventPackets
) {}
