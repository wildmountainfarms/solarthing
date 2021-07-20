package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.BatteryUtil;

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
		}
		return false;
	}
}
