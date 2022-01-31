package me.retrodaredevil.solarthing.commands.event;

import me.retrodaredevil.solarthing.annotations.WorkInProgress;
import me.retrodaredevil.solarthing.marker.EventPacket;

@WorkInProgress
public interface SecurityRejectPacket extends EventPacket {

	Reason getReason();
	String getMoreInfo();

	enum Reason {
		/** Used for {@link me.retrodaredevil.solarthing.packets.security.SecurityPacketType#LARGE_INTEGRITY_PACKET} to indicate
		 * the payload's hash did not match the encrypted hash*/
		DIFFERENT_PAYLOAD,
		INVALID_DATA,
		DECRYPT_ERROR,
		UNAUTHORIZED,
		CLOCK_VARIANCE,
	}
}
