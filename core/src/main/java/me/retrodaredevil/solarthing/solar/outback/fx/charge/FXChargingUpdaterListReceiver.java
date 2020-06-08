package me.retrodaredevil.solarthing.solar.outback.fx.charge;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class FXChargingUpdaterListReceiver implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(FXChargingUpdaterListReceiver.class);
	private final @Nullable Integer masterFXAddress;
	private final FXChargingSettings fxChargingSettings;
	private final FXChargingStateHandler fxChargingStateHandler;

	private Long lastTime = null;

	public FXChargingUpdaterListReceiver(@Nullable Integer masterFXAddress, FXChargingSettings fxChargingSettings) {
		this.masterFXAddress = masterFXAddress;
		this.fxChargingSettings = requireNonNull(fxChargingSettings);
		fxChargingStateHandler = new FXChargingStateHandler(fxChargingSettings);
	}

	@Override
	public void receive(List<Packet> packets, InstantType instantType) {
		if(masterFXAddress == null){
			FXStatusPacket fxStatusPacket = OutbackUtil.getMasterFX(packets);
			if(fxStatusPacket != null){
				handlePacket(packets, fxStatusPacket);
			}
		} else {
			for(Packet packet : packets){ // this might be modified, but that's fine because we break when we modify it
				if (packet instanceof SolarStatusPacket && packet instanceof OutbackData) {
					int address = ((OutbackData) packet).getAddress();
					if (address == masterFXAddress) {
						if (packet instanceof FXStatusPacket) {
							handlePacket(packets, (FXStatusPacket) packet);
							break;
						} else {
							LOGGER.warn("The master fx address is not an FX status packet! packet: " + packet);
						}
					}
				}
			}
		}
	}
	private void handlePacket(List<Packet> packets, FXStatusPacket fxStatusPacket){
		long now = System.currentTimeMillis();
		Long lastTime = this.lastTime;
		this.lastTime = now;
		final long delta;
		if(lastTime == null){
			delta = 1000;
		} else {
			delta = Math.min(now - lastTime, 10 * 1000);
		}
		fxChargingStateHandler.update(delta, fxStatusPacket);
		FXChargingPacket fxChargingPacket = new ImmutableFXChargingPacket(
				fxStatusPacket.getIdentifier(), fxChargingStateHandler.getMode(),
				fxChargingStateHandler.getRemainingAbsorbTimeMillis(), fxChargingStateHandler.getRemainingFloatTimeMillis(), fxChargingStateHandler.getRemainingEqualizeTimeMillis(),
				fxChargingSettings.getAbsorbTimeMillis(), fxChargingSettings.getFloatTimeMillis(), fxChargingSettings.getEqualizeTimeMillis()
		);
		packets.add(fxChargingPacket);
	}
}
