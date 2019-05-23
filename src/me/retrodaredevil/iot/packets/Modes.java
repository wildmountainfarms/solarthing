package me.retrodaredevil.iot.packets;

import java.util.EnumSet;
import java.util.Set;

public final class Modes {
	private Modes(){ throw new UnsupportedOperationException(); }

	public static <T extends Enum<T> & CodeMode> T getActiveMode(Class<T> tEnum, int valueCode, T defaultValue){
		for(T enumValue : EnumSet.allOf(tEnum)){
			if(enumValue.isActive(valueCode)){
				return enumValue;
			}
		}
		return defaultValue;
	}
	public static <T extends Enum<T> & CodeMode> T getActiveMode(Class<T> tEnum, int valueCode){
		return getActiveMode(tEnum, valueCode, null);
	}
	/**
	 *
	 * @param tEnum The class of the enum that has all the possible modes
	 * @param code The code that is bit masked
	 * @param <T> The enum that has all the possible modes
	 * @return A set with all of the modes that are active in tEnum
	 */
	public static <T extends Enum<T> & BitmaskMode> Set<T> getActiveModes(Class<T> tEnum, int code){
		final Set<T> set = EnumSet.noneOf(tEnum);
		for(T enumValue : EnumSet.allOf(tEnum)){
			if(enumValue.isActive(code)){
				set.add(enumValue);
			}
		}
		return set;
	}

	/**
	 * Possible return values: "", "Element1", "Element1, Element2, Element3", etc.
	 * @param tEnum The class of the enum that has all the possible modes
	 * @param code The code that is bit masked
	 * @param <T> The enum that has all the possible modes
	 * @return Each active mode separated by ", "
	 */
	public static <T extends Enum<T> & BitmaskMode> String toString(Class<T> tEnum, int code){
		StringBuilder builder = new StringBuilder();
		boolean empty = true;
		for(T enumValue : EnumSet.allOf(tEnum)){
			if(enumValue.isActive(code)){
				if(!empty){
					builder.append(", ");
				}
				builder.append(enumValue.getModeName());
				empty = false;
			}
		}
		return builder.toString();
	}
}
