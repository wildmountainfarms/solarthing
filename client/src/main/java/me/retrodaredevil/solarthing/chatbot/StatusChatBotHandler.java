package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.solar.BatteryUtil;
import me.retrodaredevil.solarthing.solar.common.BatteryTemperature;
import me.retrodaredevil.solarthing.type.alter.AlterPacket;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandData;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandPacket;
import me.retrodaredevil.solarthing.util.TimeUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatusChatBotHandler implements ChatBotHandler {
	private final FragmentedPacketGroupProvider packetGroupProvider;
	private final AlterPacketsProvider alterPacketsProvider;

	public StatusChatBotHandler(FragmentedPacketGroupProvider packetGroupProvider, AlterPacketsProvider alterPacketsProvider) {
		this.packetGroupProvider = packetGroupProvider;
		this.alterPacketsProvider = alterPacketsProvider;
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
		} else if (ChatBotUtil.isSimilar("alter", message.getText())) {
			List<StoredAlterPacket> alterPackets = alterPacketsProvider.getPackets();
			if (alterPackets == null) {
				messageSender.sendMessage("Error - Must have failed to query alter database");
			} else {
				List<String> scheduledCommandLines = new ArrayList<>();
				for (StoredAlterPacket storedAlterPacket : alterPackets) {
					AlterPacket alterPacket = storedAlterPacket.getPacket();
					if (alterPacket instanceof ScheduledCommandPacket) {
						ScheduledCommandData data = ((ScheduledCommandPacket) alterPacket).getData();
//						ExecutionReason executionReason = ((ScheduledCommandPacket) alterPacket).getExecutionReason();
						String timeString = TimeUtil.instantToSlackDateSeconds(Instant.ofEpochMilli(data.getScheduledTimeMillis()));
						scheduledCommandLines.add(data.getCommandName() + " - " + timeString);
					}
				}
				if (scheduledCommandLines.isEmpty()) {
					messageSender.sendMessage("No scheduled commands (from " + alterPackets.size() + " alter packets)");
				} else {
					messageSender.sendMessage("Scheduled commands:\n\t" + String.join("\n\t", scheduledCommandLines));
				}
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
