package me.retrodaredevil.solarthing.solar.outback.fx;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.solar.outback.fx.event.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Receives fx status packets and then sends them to something to handle "event" packets
 */
public class FXEventUpdaterListReceiver implements PacketListReceiver {
	private final PacketListReceiver eventReceiver;
	private final Map<Integer, Integer> fxWarningIgnoreMap;
	private final Map<Identifier, FXStatusPacket> previousPacketMap = new HashMap<>();

	public FXEventUpdaterListReceiver(PacketListReceiver eventReceiver, Map<Integer, Integer> fxWarningIgnoreMap) {
		this.eventReceiver = eventReceiver;
		this.fxWarningIgnoreMap = fxWarningIgnoreMap;
	}

	@Override
	public void receive(List<Packet> packets, boolean wasInstant) {
		for(Packet packet : packets){
			if(packet instanceof FXStatusPacket){
				FXStatusPacket fx = (FXStatusPacket) packet;
				FXStatusPacket previous = previousPacketMap.get(fx.getIdentifier());
				previousPacketMap.put(fx.getIdentifier(), fx);
				Integer warningIgnoreValue = fxWarningIgnoreMap.get(fx.getAddress());
				useData(fx, previous, warningIgnoreValue == null ? FXWarningModeChangePacket.DEFAULT_IGNORED_WARNING_MODE_VALUE_CONSTANT : warningIgnoreValue, wasInstant);
			}
		}
	}
	private void useData(FXStatusPacket fx, FXStatusPacket previous, int ignoredWarningModeValueConstant, boolean wasInstant){
		final Integer lastACMode;
		final Boolean wasAuxActive;
		final Integer previousOperationalModeValue;
		final Integer previousErrorModeValue;
		final Integer previousWarningModeValue;
		if(previous == null){
			lastACMode = null;
			wasAuxActive = null;
			previousOperationalModeValue = null;
			previousErrorModeValue = null;
			previousWarningModeValue = null;
		} else {
			lastACMode = previous.getACModeValue();
			wasAuxActive = previous.getMiscModes().contains(MiscMode.AUX_OUTPUT_ON);
			previousOperationalModeValue = previous.getOperationalModeValue();
			previousErrorModeValue = previous.getErrorModeValue();
			previousWarningModeValue = previous.getWarningModeValue();
		}
		int currentACMode = fx.getACModeValue();
		boolean isAuxActive = fx.getMiscModes().contains(MiscMode.AUX_OUTPUT_ON);
		int operationalModeValue = fx.getOperationalModeValue();
		int errorModeValue = fx.getErrorModeValue();
		int warningModeValue = fx.getWarningModeValue();
		List<Packet> packets = new ArrayList<>();
		if(lastACMode == null || currentACMode != lastACMode){
			packets.add(new ImmutableFXACModeChangePacket(fx.getIdentifier(), currentACMode, lastACMode));
		}
		if(wasAuxActive == null || isAuxActive != wasAuxActive){
			packets.add(new ImmutableFXAuxStateChangePacket(fx.getIdentifier(), isAuxActive, wasAuxActive));
		}
		if(previousOperationalModeValue == null || operationalModeValue != previousOperationalModeValue){
			packets.add(new ImmutableFXOperationalModeChangePacket(fx.getIdentifier(), operationalModeValue, previousOperationalModeValue));
		}
		if(previousErrorModeValue == null || errorModeValue != previousErrorModeValue){
			packets.add(new ImmutableFXErrorModeChangePacket(fx.getIdentifier(), errorModeValue, previousErrorModeValue));
		}
		if(previousWarningModeValue == null || (warningModeValue & ~ignoredWarningModeValueConstant) != (previousWarningModeValue & ~ignoredWarningModeValueConstant)) {
			packets.add(new ImmutableFXWarningModeChangePacket(fx.getIdentifier(), warningModeValue, previousWarningModeValue, ignoredWarningModeValueConstant));
		}
		if(!packets.isEmpty()){
			eventReceiver.receive(packets, wasInstant);
		}
	}
}
