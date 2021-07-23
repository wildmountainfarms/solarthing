package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.solar.BatteryUtil;
import me.retrodaredevil.solarthing.solar.common.BatteryTemperature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatusChatBotHandler implements ChatBotHandler {
	private final FragmentedPacketGroupProvider packetGroupProvider;

	public StatusChatBotHandler(FragmentedPacketGroupProvider packetGroupProvider) {
		this.packetGroupProvider = packetGroupProvider;
	}

	@Override
	public boolean handleMessage(Message message, MessageSender messageSender) {
		if (ChatBotUtil.isSimilar("battery voltage", message.getText())) {
			FragmentedPacketGroup packetGroup = packetGroupProvider.getPacketGroup();
			Float batteryVoltageAverage = BatteryUtil.getBatteryVoltageAverage(packetGroup);
			if (batteryVoltageAverage == null) {
				messageSender.sendMessage("Battery voltage unavailable from latest data. Sorry!");
			} else {
				messageSender.sendMessage("Battery voltage: " + Formatting.HUNDREDTHS_FORMAT.format(batteryVoltageAverage));
			}
			return true;
		} else if (ChatBotUtil.isSimilar("battery temperature", message.getText()) || ChatBotUtil.isSimilar("battery temp", message.getText())) {
			FragmentedPacketGroup packetGroup = packetGroupProvider.getPacketGroup();
			List<String> lines = new ArrayList<>();
			for (Packet packet : packetGroup.getPackets()) {
				int fragmentId = packetGroup.getFragmentId(packet);
				if (packet instanceof BatteryTemperature && packet instanceof Identifiable) {
					float temperature = ((BatteryTemperature) packet).getBatteryTemperatureFahrenheit();
					IdentityInfo identityInfo = ((Identifiable) packet).getIdentityInfo();
					lines.add(identityInfo.getDisplayName() + " (" + fragmentId + "): " + temperature + "F");
				}
			}
			if (lines.isEmpty()) {
				messageSender.sendMessage("No devices to read temperature!");
			} else {
				messageSender.sendMessage(String.join("\n", lines));
			}
			return true;
		}
		return false;
	}

	@Override
	public @NotNull List<String> getHelpLines(Message helpMessage) {
		return Arrays.asList(
				"\"battery voltage\" -- Get the battery voltage",
				"\"battery temperature\" -- Get the battery temperature from each device"
		);
	}
}
