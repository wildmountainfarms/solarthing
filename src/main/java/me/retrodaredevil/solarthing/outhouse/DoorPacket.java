package me.retrodaredevil.solarthing.outhouse;

public interface DoorPacket extends OuthousePacket {
	boolean isOpen();
	Long getLastCloseTimeMillis();
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
