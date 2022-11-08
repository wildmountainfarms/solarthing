package me.retrodaredevil.solarthing.actions.error;

public final class ActionErrorState {

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
