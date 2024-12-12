package me.retrodaredevil.solarthing.program.subprogram.analyze.analyzers.generator;

import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;
import me.retrodaredevil.solarthing.program.subprogram.analyze.Analyzer;
import me.retrodaredevil.solarthing.program.subprogram.analyze.DataChunk;
import me.retrodaredevil.solarthing.program.subprogram.analyze.analyzers.generator.entry.GeneratorRunEntry;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * An analyzer that is designed to analyze individual generator runs and output statistics about each run.
 */
public class GeneratorRunAnalyzer implements Analyzer<GeneratorRunEntry> {

	private final DefaultInstanceOptions defaultInstanceOptions;
	private final int fragmentId;

	public GeneratorRunAnalyzer(DefaultInstanceOptions defaultInstanceOptions, int fragmentId) {
		this.defaultInstanceOptions = requireNonNull(defaultInstanceOptions);
		this.fragmentId = fragmentId;
	}

	/**
	 * Analyzes the row given data.
	 * <p>
	 * Note that the implementation should copy any data necessary, as the caller may mutate the passed arguments after the call.
	 * @param packets A list of FX packets
	 * @return The {@link GeneratorRunEntry} created from the passed arguments
	 */
	private GeneratorRunEntry analyzeRow(List<InstancePacketGroup> packets) {
		var first = packets.get(0);
		var last = packets.get(packets.size() - 1);
		return new GeneratorRunEntry(
				Instant.ofEpochMilli(first.getDateMillis()),
				Instant.ofEpochMilli(last.getDateMillis())
		);
	}

	@Override
	public List<GeneratorRunEntry> analyze(Instant dataStartInstant, Instant processingStartInstant, Instant processingEndInstant, Instant dataEndInstant, DataChunk data) {
		long processingStartMillis = processingStartInstant.toEpochMilli();
		long processingEndMillis = processingEndInstant.toEpochMilli();
		List<GeneratorRunEntry> rows = new ArrayList<>();
		List<InstancePacketGroup> packetGroups = new ArrayList<>();

		boolean startedInsideRange = false;
		boolean wasGeneratorOn = false;

		for (StoredPacketGroup storedPacketGroup : data.statusPackets()) {
			InstancePacketGroup instancePacketGroup = PacketGroups.parseToInstancePacketGroup(storedPacketGroup, defaultInstanceOptions);
			long dateMillis = instancePacketGroup.getDateMillis();

			// TODO filtering out other fragment IDs is a great start for a POC, but we eventually need to do something like
			//   mergePackets because while we want to focus on this fragment ID, we also want data from other fragments.
			if (instancePacketGroup.getFragmentId() != fragmentId) {
				continue;
			}
			List<FXStatusPacket> fxStatusPackets = instancePacketGroup.getPackets().stream()
					.filter(packet -> packet instanceof FXStatusPacket)
					.map(packet -> (FXStatusPacket) packet)
					.toList();
			if (fxStatusPackets.isEmpty()) {
				System.err.println("[warning] No fx status packets found for dateMillis: " + dateMillis);
				continue;
			}
			FXStatusPacket fx1 = fxStatusPackets.get(0);
			boolean generatorOn = fx1.getACMode() != ACMode.NO_AC;

			boolean isInsideTimeRange = dateMillis >= processingStartMillis && dateMillis < processingEndMillis;

			if (generatorOn) {
				if (!wasGeneratorOn) {
					startedInsideRange = isInsideTimeRange;
				}
				packetGroups.add(instancePacketGroup);
			} else {
				if (wasGeneratorOn) { // generator just turned off
					if (startedInsideRange) {
						rows.add(analyzeRow(packetGroups));
					}
					packetGroups.clear();
				}

				if (dateMillis >= processingEndMillis) {
					break;
				}
			}

			wasGeneratorOn = generatorOn;
		}
		return rows;
	}

}
