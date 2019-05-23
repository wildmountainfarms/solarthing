package me.retrodaredevil.iot.packets;

public interface Mode {
	boolean isActive(int code);
	String getModeName();

}
