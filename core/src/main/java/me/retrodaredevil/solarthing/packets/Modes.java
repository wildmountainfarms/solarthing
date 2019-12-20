package me.retrodaredevil.solarthing.packets;

import java.util.*;

public final class Modes {
	private Modes(){ throw new UnsupportedOperationException(); }
	
	// region CodeMode
	/**
	 * @param tEnum The enum to get the active mode from
	 * @param valueCode The code to match with {@link CodeMode#getValueCode()}
	 * @param defaultValue The value to return if not found
	 * @param <T> The type of the {@link CodeMode}
	 * @return The active mode or the default value
	 */
	public static <T extends Enum<T> & CodeMode> T getActiveMode(Class<T> tEnum, int valueCode, T defaultValue){
		return getActiveMode(EnumSet.allOf(tEnum), valueCode, defaultValue);
	}
	
	
	/**
	 * Gets the active mode from a collection of modes.
	 * @param values The collection of possible values to find the correct value for
	 * @param valueCode The code to match with {@link CodeMode#getValueCode()}
	 * @param <T> The type of the {@link CodeMode}
	 * @return A value from {@code values} with the {@link CodeMode} matching {@code valueCode} or null if not found.
	 * @throws NullPointerException optional. May be thrown if {@code values} contains a null element
	 */
	public static <T extends CodeMode> T getActiveMode(Collection<? extends T> values, int valueCode, T defaultValue){
		for(T value : values){
			if(value.isActive(valueCode)){
				return value;
			}
		}
		return defaultValue;
	}
	/**
	 * @param tEnum The enum to get the active mode from
	 * @param valueCode The code to match with {@link CodeMode#getValueCode()}
	 * @param <T> The type of the {@link CodeMode}
	 * @return The active mode or null if not found
	 */
	public static <T extends Enum<T> & CodeMode> T getActiveModeOrNull(Class<T> tEnum, int valueCode){
		return getActiveMode(tEnum, valueCode, null);
	}
	/**
	 * @param tEnum The enum to get the active mode from
	 * @param valueCode The code to match with {@link CodeMode#getValueCode()}
	 * @param <T> The type of the {@link CodeMode}
	 * @return The active mode
	 * @throws NoSuchElementException thrown if the active mode was not found
	 */
	public static <T extends Enum<T> & CodeMode> T getActiveMode(Class<T> tEnum, int valueCode){
		T r = getActiveMode(tEnum, valueCode, null);
		if(r == null){
			throw new NoSuchElementException("valueCode: " + valueCode + " not found in enum: " + tEnum);
		}
		return r;
	}
	/**
	 * Gets the active mode from a collection of modes.
	 * @param values The collection of possible values to find the correct value for
	 * @param valueCode The code to match with {@link CodeMode#getValueCode()}
	 * @param <T> The type of the {@link CodeMode}
	 * @return A value from {@code values} with the {@link CodeMode} matching {@code valueCode} or returns null if not found.
	 * @throws NullPointerException optional. May be thrown if {@code values} contains a null element
	 */
	@SuppressWarnings("unused")
	public static <T extends CodeMode> T getActiveModeOrNull(Collection<? extends T> values, int valueCode){
		return getActiveMode(values, valueCode, null);
	}
	
	/**
	 * Gets the active mode from a collection of modes.
	 * @param values The collection of possible values to find the correct value for
	 * @param valueCode The code to match with {@link CodeMode#getValueCode()}
	 * @param <T> The type of the {@link CodeMode}
	 * @return A non-null value from {@code values} with the {@link CodeMode} matching {@code valueCode}
	 * @throws NullPointerException optional. May be thrown if {@code values} contains a null element
	 * @throws NoSuchElementException thrown if no {@link CodeMode} is found in {@code values} that corresponds to {@code valueCode}
	 */
	public static <T extends CodeMode> T getActiveMode(Collection<? extends T> values, int valueCode){
		T r = getActiveMode(values, valueCode, null);
		if(r == null){
			throw new NoSuchElementException("valueCode: " + valueCode + " not found in values: " + values);
		}
		return r;
	}
	// endregion

	// region CodeMode multiple
	public static <T extends CodeMode> void getActiveModes(Collection<? extends T> possibleValues, Collection<Integer> valueCodes, Collection<? super T> mutableActiveModes){
		for(T t : possibleValues){
			if(valueCodes.contains(t.getValueCode())){
				mutableActiveModes.add(t);
			}
		}
	}
	public static <T extends Enum<T> & CodeMode> Set<T> getActiveModes(Class<T> tEnum, Collection<Integer> valueCodes){
		final Set<T> set = EnumSet.noneOf(tEnum);
		getActiveModes(EnumSet.allOf(tEnum), valueCodes, set);
		return set;
	}
	@SuppressWarnings("unused")
	public static <T extends CodeMode> Set<T> getActiveModes(Collection<? extends T> possibleValues, Collection<Integer> valueCodes){
		final Set<T> set = new HashSet<>();
		getActiveModes(possibleValues, valueCodes, set);
		return set;
	}
	// endregion
	
	// region BitmaskMode
	/**
	 * @param tEnum The class of the enum that has all the possible modes
	 * @param code The code that is bit masked
	 * @param <T> The enum that has all the possible modes
	 * @return A set with all of the modes that are active in tEnum
	 */
	public static <T extends Enum<T> & BitmaskMode> Set<T> getActiveModes(Class<T> tEnum, int code){
		final Set<T> set = EnumSet.noneOf(tEnum);
		getActiveModes(EnumSet.allOf(tEnum), code, set);
		return set;
	}
	/**
	 * @param possibleValues The collection of possible values to return
	 * @param code The code that is bit masked
	 * @param <T> The enum that has all the possible modes
	 * @return A new set with all of the modes that are active in tEnum
	 */
	@SuppressWarnings("unused")
	public static <T extends BitmaskMode> Set<T> getActiveModes(Collection<? extends T> possibleValues, int code){
		final Set<T> set = new HashSet<>();
		getActiveModes(possibleValues, code, set);
		return set;
	}
	/**
	 * @param possibleValues The possible values to add to {@code mutableActiveModes}
	 * @param code The code
	 * @param mutableActiveModes A {@link Collection} that will be mutated to contain all the values of {@code possibleValues} that are active based on {@code code}
	 * @param <T> The type of the {@link BitmaskMode}
	 */
	public static <T extends BitmaskMode> void getActiveModes(Collection<? extends T> possibleValues, int code, Collection<? super T> mutableActiveModes){
		for(T value : possibleValues){
			if(value.isActive(code)){
				mutableActiveModes.add(value);
			}
		}
	}
	// endregion
	
	// region toString

	public static <T extends CodeMode> String toString(Collection<? extends T> possibleValues, Collection<Integer> valueCodes){
		StringBuilder builder = new StringBuilder();
		boolean empty = true;
		for(T value : possibleValues){
			if(valueCodes.contains(value.getValueCode())){
				if(!empty){
					builder.append(", ");
				}
				builder.append(value.getModeName());
				empty = false;
			}
		}
		return builder.toString();
	}
	public static <T extends Enum<T> & CodeMode> String toString(Class<T> tEnum, Collection<Integer> valueCodes){
		return toString(EnumSet.allOf(tEnum), valueCodes);
	}

	/**
	 * @see #toString(Collection, int)
	 */
	public static <T extends Enum<T> & BitmaskMode> String toString(Class<T> tEnum, int code){
		return toString(EnumSet.allOf(tEnum), code);
	}
	/**
	 * Possible return values: "", "Element1", "Element1, Element2, Element3", etc.
	 * @param values A Collection of all the possible {@link BitmaskMode}s
	 * @param code The code that is bit masked
	 * @param <T> The enum that has all the possible modes
	 * @return Each active mode separated by ", "
	 */
	public static <T extends BitmaskMode> String toString(Collection<? extends T> values, int code){
		StringBuilder builder = new StringBuilder();
		boolean empty = true;
		for(T value : values){
			if(value.isActive(code)){
				if(!empty){
					builder.append(", ");
				}
				builder.append(value.getModeName());
				empty = false;
			}
		}
		return builder.toString();
	}
	// endregion
}
