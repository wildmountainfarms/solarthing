package me.retrodaredevil.solarthing.config.request.modbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ReloadIOSuccessReporterHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReloadIOSuccessReporterHandler.class);
	private final Runnable reloadIO;

	private final Map<Object, Integer> reporterToErrorCountMap = new HashMap<>();

	public ReloadIOSuccessReporterHandler(Runnable reloadIO) {
		this.reloadIO = reloadIO;
	}
	public SuccessReporter createReporter() {
		UniqueReporter reporter = new UniqueReporter();
		reporterToErrorCountMap.put(reporter, 0);
		return reporter;
	}

	private void doReload() {
		LOGGER.debug("Going to try to recreate IO now. We failed a bunch of times in a row");
		reloadIO.run();
		reporterToErrorCountMap.entrySet().forEach(entry -> entry.setValue(0));
	}


	private class UniqueReporter implements SuccessReporter {

		@Override
		public void reportSuccess() {
			reporterToErrorCountMap.put(this, 0);
		}

		@Override
		public void reportTimeout() {
			int errorsInARow = reporterToErrorCountMap.compute(this, (_this, currentValue) -> requireNonNull(currentValue, "null value for this: " + this + "!") + 1);

			if (errorsInARow >= 5) {
				int size = reporterToErrorCountMap.size();
				if (size <= 2) {
					doReload();
				} else {
					boolean othersHaveConsistentFailing = reporterToErrorCountMap.entrySet().stream()
							.filter(entry -> entry.getKey() != this)
							.anyMatch(entry -> entry.getValue() >= 2);
					if (othersHaveConsistentFailing) {
						doReload();
					}
				}
			}
		}

		@Override
		public void reportSuccessWithError() {
			reportSuccess(); // do the same thing
		}
	}
}
