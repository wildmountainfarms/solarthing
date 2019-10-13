package me.retrodaredevil.solarthing.outhouse;

public interface DoorPacket extends OuthousePacket {
	/**
	 * Should be serialized as "isOpen"
	 * @return
	 */
	boolean isOpen();
	
	/**
	 * Should be serialized as "lastCloseTimeMillis"
	 * @return
	 */
	Long getLastCloseTimeMillis();
	/**
	 * Should be serialized as "lastOpenTimeMillis"
	 * @return
	 */
	Long getLastOpenTimeMillis();
	
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
