package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

import static java.util.Objects.requireNonNull;

public class ThrottleFactorPacketHandler implements PacketHandler {
	private final PacketHandler packetHandler;
	private final FrequencySettings frequencySettings;
	private final boolean instantOnly;

	private int initialCounter = 0;
	private int counter = 0;

	/**
	 * @param packetHandler The packet handler
	 * @param frequencySettings The frequency settings
	 * @param instantOnly true if neither {@code packetHandler} nor {@code otherPacketHandler} should be called if the packets are not INSTANT
	 */
	public ThrottleFactorPacketHandler(PacketHandler packetHandler, FrequencySettings frequencySettings, boolean instantOnly) {
		this.packetHandler = requireNonNull(packetHandler);
		this.frequencySettings = frequencySettings;
		this.instantOnly = instantOnly;
	}

	@Override
	public void handle(PacketCollection packetCollection, InstantType instantType) throws PacketHandleException {
		if(instantOnly && !instantType.isInstant()){
			return; // return and don't increment counter
		}
		if(initialCounter < frequencySettings.getInitialSkipFactor()){
			initialCounter++;
			return;
		}
		if(counter++ % frequencySettings.getThrottleFactor() == 0){
			packetHandler.handle(packetCollection, instantType);
		}
	}
}
