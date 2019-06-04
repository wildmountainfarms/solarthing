package me.retrodaredevil.solarthing.packets;

/**
 * A mode represents the status of something. Mode values are usually defined in enums
 */
public interface Mode {
	boolean isActive(int code);
	String getModeName();
}
