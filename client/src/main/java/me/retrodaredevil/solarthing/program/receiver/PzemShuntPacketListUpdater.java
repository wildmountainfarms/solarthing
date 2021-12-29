package me.retrodaredevil.solarthing.program.receiver;

import me.retrodaredevil.io.modbus.ModbusRuntimeException;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.solar.pzem.ImmutablePzemShuntStatusPacket;
import me.retrodaredevil.solarthing.solar.pzem.PzemShuntReadTable;
import me.retrodaredevil.solarthing.solar.pzem.PzemShuntStatusPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PzemShuntPacketListUpdater implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(PzemShuntPacketListUpdater.class);
	private final int dataId;
	private final int modbusAddress;
	private final PzemShuntReadTable read;

	public PzemShuntPacketListUpdater(int dataId, int modbusAddress, PzemShuntReadTable read) {
		this.dataId = dataId;
		this.modbusAddress = modbusAddress;
		this.read = read;
	}

	@Override
	public void receive(List<Packet> packets) {
		final PzemShuntStatusPacket packet;
		try {
			packet = ImmutablePzemShuntStatusPacket.createFromReadTable(dataId, modbusAddress, read);
		} catch (ModbusRuntimeException ex) {
			LOGGER.error("Modbus exception", ex);
			return;
		}
		packets.add(packet);
	}
}
