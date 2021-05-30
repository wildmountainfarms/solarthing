package me.retrodaredevil.solarthing.config.request.modbus;

public interface SuccessReporter {
	/**
	 * Reports the successful communication
	 */
	void reportSuccess();

	/**
	 * Should be used when there is no communication with a given device
	 */
	void reportTimeout();

	/**
	 * Reports successful communication, even if parsing the response results in an error
	 */
	void reportSuccessWithError();
}
