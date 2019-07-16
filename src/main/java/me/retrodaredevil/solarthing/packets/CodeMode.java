package me.retrodaredevil.solarthing.packets;

/**
 * Represents a mode where only one of its kind can be active
 */
public interface CodeMode extends Mode {
	/**
	 * @return The code representing the mode
	 */
	int getValueCode();
	

	@Override
	default boolean isActive(int valueCode){
		return getValueCode() == (getIgnoredBits() ^ valueCode);
	}
	
	default int getIgnoredBits(){
		return 0;
	}

}
