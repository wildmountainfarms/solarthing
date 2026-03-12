package me.retrodaredevil.solarthing.solar.outback.fx;


import me.retrodaredevil.solarthing.packets.CodeMode;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

/**
 * The AC Mode for the FX
 */
@NullMarked
public enum ACMode implements CodeMode { // one must be active
	NO_AC(0, "No AC"),
	AC_DROP(1, "AC Drop"),
	AC_USE(2, "AC Use");

	private final int value;
	private final String name;
	ACMode(int value, String name){
		this.value = value;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int getValueCode() {
		return value;
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getModeName() {
		return name;
	}
}
