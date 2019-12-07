package me.retrodaredevil.solarthing.outhouse;

public interface DoorPacket extends OuthousePacket {
	/**
	 * Should be serialized as "isOpen"
	 * @return true if door is open, false otherwise
	 */
	boolean isOpen();
	
	/**
	 * Should be serialized as "lastCloseTimeMillis"
	 * @return The last time the door was closed in milliseconds, or null if unknown.
	 */
	Long getLastCloseTimeMillis();
	/**
	 * Should be serialized as "lastOpenTimeMillis"
	 * @return The last time the door was opened in milliseconds, or null if unknown.
	 */
	Long getLastOpenTimeMillis();

	@SuppressWarnings("unused")
	default Long getLastActivity(){
		Long close = getLastCloseTimeMillis();
		Long open = getLastOpenTimeMillis();
		if(close == null){
			return open;
		}
		if(open == null){
			return close;
		}
		return Math.max(open, close);
	}
}
