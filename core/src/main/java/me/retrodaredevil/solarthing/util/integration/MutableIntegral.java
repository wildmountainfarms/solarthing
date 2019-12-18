package me.retrodaredevil.solarthing.util.integration;

public interface MutableIntegral {
	void add(double x, double y);
	void reset(boolean fullReset);
	default void reset(){ reset(false); }

	double getIntegral();
}
