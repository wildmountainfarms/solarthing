package me.retrodaredevil.solarthing.packets.handling;

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
	 * @param instantOnly true if neither {@code packetHandler} nor {@code otherPacketHandler} should be called if {@code wasInstant} is false
	 */
	public ThrottleFactorPacketHandler(PacketHandler packetHandler, FrequencySettings frequencySettings, boolean instantOnly) {
		this.packetHandler = requireNonNull(packetHandler);
		this.frequencySettings = frequencySettings;
		this.instantOnly = instantOnly;
	}
	/**
	 * @param packetHandler The packet handler
	 * @param throttleFactor The throttle factor. {@code packetHandler} will be called every nth packet, where n is this value.
	 * @param instantOnly true if neither {@code packetHandler} nor {@code otherPacketHandler} should be called if {@code wasInstant} is false
	 */
	@Deprecated
	public ThrottleFactorPacketHandler(PacketHandler packetHandler, int throttleFactor, boolean instantOnly) {
		this(packetHandler, new FrequencySettings(throttleFactor, 0), instantOnly);
	}
	
	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) throws PacketHandleException {
		if(instantOnly && !wasInstant){
			return; // return and don't increment counter
		}
		if(initialCounter++ < frequencySettings.getInitialSkipFactor()){
			return;
		}
		if(counter++ % frequencySettings.getThrottleFactor() == 0){
			packetHandler.handle(packetCollection, wasInstant);
		}
	}
}
