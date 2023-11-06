package me.retrodaredevil.solarthing.packets.handling;

/**
 * You should throw this exception when you want the message that is logged to be a debug message, rather than an important message like info, warn or error
 */
public class CommonPacketHandleException extends PacketHandleException {
	public CommonPacketHandleException() {
	}

	public CommonPacketHandleException(String message) {
		super(message);
	}

	public CommonPacketHandleException(String message, Throwable cause) {
		super(message, cause);
	}
}
