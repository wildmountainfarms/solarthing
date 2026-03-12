package me.retrodaredevil.solarthing.packets.handling;

import org.jspecify.annotations.NullMarked;

/**
 * You should throw this exception when you want the message that is logged to be a debug message, rather than an important message like info, warn or error
 */
@NullMarked
public class CommonPacketHandleException extends PacketHandleException {
	public CommonPacketHandleException(String message) {
		super(message);
	}

	public CommonPacketHandleException(String message, Throwable cause) {
		super(message, cause);
	}
}
