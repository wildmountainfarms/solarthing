package me.retrodaredevil.solarthing.actions.rover;

public final class RoverActionErrorState {

	private int errorCount = 0;
	private int successCount = 0;

	public int getErrorCount() {
		return errorCount;
	}
	public void incrementErrorCount() {
		errorCount++;
	}

	public int getSuccessCount() {
		return successCount;
	}
	public void incrementSuccessCount() {
		successCount++;
	}
}
