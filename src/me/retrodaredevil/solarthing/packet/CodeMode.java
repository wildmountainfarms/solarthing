package me.retrodaredevil.solarthing.packet;

public interface CodeMode extends Mode {
	int getValueCode();

	@Override
	default boolean isActive(int valueCode){
		return getValueCode() == valueCode;
	}

}
