package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

import static java.util.Objects.requireNonNull;

public class ThrottleFactorPacketHandler implements PacketHandler {
	private final PacketHandler packetHandler;
	private final FrequencySettings frequencySettings;

	private int initialCounter = 0;
	private int counter = 0;

	/**
	 * @param packetHandler The packet handler
	 * @param frequencySettings The frequency settings
	 */
	public ThrottleFactorPacketHandler(PacketHandler packetHandler, FrequencySettings frequencySettings) {
		this.packetHandler = requireNonNull(packetHandler);
		this.frequencySettings = requireNonNull(frequencySettings);
	}

	@Override
	public void handle(PacketCollection packetCollection) throws PacketHandleException {
		if(initialCounter < frequencySettings.getInitialSkipFactor()){
			initialCounter++;
			return;
		}
		if(counter++ % frequencySettings.getThrottleFactor() == 0){
			packetHandler.handle(packetCollection);
		}
	}
}
