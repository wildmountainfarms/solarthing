package me.retrodaredevil.solarthing.packet;

public interface Mode {
	boolean isActive(int code);
	String getModeName();

}
