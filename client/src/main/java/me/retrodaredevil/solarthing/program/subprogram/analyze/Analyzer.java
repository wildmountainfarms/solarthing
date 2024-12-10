package me.retrodaredevil.solarthing.program.subprogram.analyze;

import java.time.Instant;
import java.util.List;

public interface Analyzer<RESULT> {
	/**
	 * An implementation should analyze the given {@link DataChunk} while taking the {@code processingStartInstant} and {@code processingEndInstant} constraints into account.
	 * <p>
	 * {@code dataStartInstant <= processingStartInstant < processingEndInstant < dataEndInstant} must hold true when calling this method.
	 * If called incorrectly, undefined behavior may occur depending on the implementation.
	 * <p>
	 * <br/>
	 * <p>
	 * The simplest example of how an implementation may function and what the purpose of including data that is outside the processing window is that of a generator run analysis.
	 * Imagine that a generator run will never be longer than 12 hours at a time.
	 * When we ask an {@link Analyzer} to analyze many 24-hour periods, we may expect that a single generator run may span two different days (two different 24-hour periods).
	 * To account for this, we need to give the {@link Analyzer} about 36 hours worth of data. We then expect the implementation to only analyze and output
	 * generator runs that <strong>started</strong> within the processing window.
	 * <p>
	 * This works great! We can now expect that analyzing generator runs that span two days will have all the data needed.
	 * Now, there's still another problem, what about when we try to analyze day 2 (the second day that the generator run in question spans)?
	 * A bad implementation will now count the tail end of that generator run as its own, shorter run!
	 * We account for this by giving the implementation data before the processing window and expect the implementation
	 * to disregard generator runs that started before the processing window.
	 * <p>
	 * With data before and after the processing window, we expect implementations to be able to reliably count and analyze generator runs that span multiple days.
	 * Note that depending on the kind of analysis you want to do, data before and after the processing window may not be necessary.
	 * In some cases you may actually need to increase the data outside the processing window, or increase the data outside the processing window and the processing window itself.
	 *
	 *
	 * @param dataStartInstant The minimum (inclusive) instant which data may have been retrieved from
	 * @param processingStartInstant The minimum (inclusive) instant in time that data should start being processed at
	 * @param processingEndInstant The maximum (exclusive) instant in time that data should stop being processed at
	 * @param dataEndInstant The maximum (exclusive) instant which data may have been retrieved from
	 * @param data The chunk of data containing data between {@code dataStartInstant} and {@code dataEndInstant}
	 * @return A list of entries resulting from the analysis of data between {@code processingStartInstant} and {@code processingEndInstant}
	 */
	List<RESULT> analyze(Instant dataStartInstant, Instant processingStartInstant, Instant processingEndInstant, Instant dataEndInstant, DataChunk data);
}
