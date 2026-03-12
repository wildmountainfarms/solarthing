package me.retrodaredevil.solarthing.packets;

import org.jspecify.annotations.NullMarked;

/**
 * Represents a mode where only one of its kind can be active
 */
@NullMarked
public interface CodeMode extends Mode {
	/**
	 * @return The code representing the mode
	 */
	int getValueCode();


	@Override
	default boolean isActive(int valueCode){
		return getValueCode() == valueCode;
	}
}
