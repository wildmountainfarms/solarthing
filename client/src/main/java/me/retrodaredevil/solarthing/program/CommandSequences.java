package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.solarthing.commands.sequence.CommandSequence;
import me.retrodaredevil.solarthing.commands.sequence.condition.Conditions;
import me.retrodaredevil.solarthing.commands.sequence.condition.TimedCondition;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;

final class CommandSequences {
	public static CommandSequence<MateCommand> createAuxGeneratorShutOff(PacketCollectionProvider packetCollectionProvider){
		return new CommandSequence.Builder<MateCommand>()
			.append(Conditions.IMMEDIATE, MateCommand.DROP)
			.append(new TimedCondition(2000), MateCommand.AUX_ON)
			.append(Conditions.create(() -> {
				PacketCollection packetCollection = packetCollectionProvider.getPacketCollection();
				if(packetCollection == null){
					throw new IllegalStateException("latestPacketHandler should have already stored a latest packet!");
				}
				FXStatusPacket fx = OutbackUtil.getMasterFX(packetCollection);
				if(fx == null){
					System.err.println("No master FX in PacketCollection at: " + packetCollection.getDateMillis());
					return false;
				}
				return ACMode.NO_AC.isActive(fx.getACModeValue());
			}), MateCommand.AUX_OFF)
			.append(Conditions.IMMEDIATE, MateCommand.USE)
			.build();
	}
	public interface PacketCollectionProvider {
		/**
		 *
		 * @return The latest PacketCollection or null if one has not been received yet
		 */
		PacketCollection getPacketCollection();
	}
}
