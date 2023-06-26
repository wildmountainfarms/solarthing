package me.retrodaredevil.solarthing.analytics;

import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.config.options.ProgramType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalyticsManager {
	private static final String ANALYTICS_NOTE = "(Note) For this SolarThing version, analytics are not sent. This will be changed in a future version. If you see this log message, a future version of SolarThing may send analytics data (if you decide to update).";
	private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsManager.class);
	private final boolean isEnabled;

	/**
	 * Constructs the object. No connections should be created here and no analytics collection should be done just because the constructor was invoked.
	 *
	 * @param isEnabled true if analytics are enabled, false otherwise
	 */
	public AnalyticsManager(boolean isEnabled) {
		this.isEnabled = isEnabled;
		if (isEnabled) {
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Analytics are ENABLED! " + ANALYTICS_NOTE);
		} else {
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Analytics are disabled");
		}
	}

	public void sendStartUp(ProgramType programType) {
		if (isEnabled) {
			LOGGER.info("Sending program type to analytics (" + programType + ") " + ANALYTICS_NOTE);
		}
	}
	public void sendMateStatus(String data, int uptimeHours) {
		if (isEnabled) {
			LOGGER.info("Sending Mate status to analytics. data=" + data + " uptime hours=" + uptimeHours + " " + ANALYTICS_NOTE);
		}
	}
	public void sendRoverStatus(String data, int uptimeHours) {
		if (isEnabled) {
			LOGGER.info("Sending Rover status to analytics. data=" + data + " uptime hours=" + uptimeHours + " " + ANALYTICS_NOTE);
		}
	}
}
