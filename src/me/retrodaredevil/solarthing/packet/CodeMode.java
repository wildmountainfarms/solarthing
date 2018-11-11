package me.retrodaredevil.solarthing.packet;

import java.util.EnumSet;

public interface CodeMode extends Mode {
	int getValueCode();

	@Override
	default boolean isActive(int valueCode){
		return getValueCode() == valueCode;
	}

	static <T extends Enum<T> & CodeMode> T getActiveMode(Class<T> tEnum, int valueCode){
		for(T enumValue : EnumSet.allOf(tEnum)){
			if(enumValue.isActive(valueCode)){
				return enumValue;
			}
		}
		return null;
	}
}
