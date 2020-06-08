package me.retrodaredevil.solarthing.analytics;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;

import java.util.ArrayList;
import java.util.List;

public class MateAnalyticsHandler implements PacketHandler {
	private final AnalyticsManager analyticsManager;
	private final AnalyticsTimer timer = new AnalyticsTimer();


	public MateAnalyticsHandler(AnalyticsManager analyticsManager) {
		this.analyticsManager = analyticsManager;
	}

	@Override
	public void handle(PacketCollection packetCollection, InstantType instantType) {
		if (!instantType.isInstant()) {
			return;
		}
		if(timer.shouldSend()) {
			timer.onSend();
			send(packetCollection);
		}
	}
	private void send(PacketCollection packetCollection) {
		int fxCount = 0;
		List<Integer> fxOperationalModes = new ArrayList<>();
		int mxfmCount = 0;
		int fmCount = 0;
		Boolean isOldFirmware = null;
		for (Packet packet : packetCollection.getPackets()) {
			if (packet instanceof FXStatusPacket) {
				fxCount++;
				FXStatusPacket fx = (FXStatusPacket) packet;
				fxOperationalModes.add(fx.getOperationalModeValue());
			} else if (packet instanceof MXStatusPacket) {
				mxfmCount++;
				MXStatusPacket mx = (MXStatusPacket) packet;
				if (Boolean.TRUE.equals(mx.isFlexMax())) {
					fmCount++;
				}
				Boolean oldMate = mx.isOldMateFirmware();
				if (Boolean.TRUE.equals(oldMate)) {
					isOldFirmware = true;
				} else if (Boolean.FALSE.equals(oldMate)) {
					isOldFirmware = false;
				}
			}
		}
		String data = "";
		if (fxCount > 0) {
			data += "fx=" + fxCount + ",fx_op=" + fxOperationalModes + ",";
		}
		if (mxfmCount > 0) {
			data += "mxfm=" + mxfmCount + ",";
		}
		if (fmCount > 0) {
			data += "fm=" + fmCount + ",";
		}
		if (isOldFirmware != null) {
			if (isOldFirmware) {
				data += "old_mate,";
			} else {
				data += "new_mate,";
			}
		}
		if (!data.isEmpty()) {
			data = data.substring(0, data.length() - 1); // remove comma
		}
		analyticsManager.sendMateStatus(data, timer.getUptimeHours());
	}
}
