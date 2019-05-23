package me.retrodaredevil.iot.packets;

public interface CodeMode extends Mode {
	/**
	 * NOTE: -1 <em>usually</em> represents an unknown mode
	 * @return The code representing the mode
	 */
	int getValueCode();

	@Override
	default boolean isActive(int valueCode){
		return getValueCode() == valueCode;
	}

}
