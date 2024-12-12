package me.retrodaredevil.solarthing.program.subprogram.analyze;

import me.retrodaredevil.solarthing.database.MillisQuery;
import me.retrodaredevil.solarthing.database.MillisQueryBuilder;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * The job of the AnalysisExecutor is to gather data from a period of time, and instruct an {@link Analyzer} to analyze that chunk of data.
 * <p>
 * Each {@link Analyzer} is designed to be instructed to analyze only a portion of the data provided to it.
 * For the generator run analysis example, you would give its analyzer 2 days worth of data, and then instruct it to only analyze generator runs that started on the first day.
 * This is done to account for generator runs (or other sections of data) that may need to be analyzed over the period of multiple days.
 */
public class AnalysisExecutor<T> {
	private static final int MAX_RETRIES = 10;

	/** The SolarThing database instance */
	private final SolarThingDatabase database;
	/** The analyzer */
	private final Analyzer<? extends T> analyzer;
	/** The processing window */
	private final Duration processingWindowDuration;
	/** The duration after the processing window to retrieve data for*/
	private final Duration beforeProcessingWindowFetchDuration;
	/** The duration before the processing window to retrieve data for*/
	private final Duration afterProcessingWindowFetchDuration;

	public AnalysisExecutor(SolarThingDatabase database, Analyzer<? extends T> analyzer, Duration processingWindowDuration, Duration beforeProcessingWindowFetchDuration, Duration afterProcessingWindowFetchDuration) {
		this.database = requireNonNull(database);
		this.analyzer = requireNonNull(analyzer);
		if (processingWindowDuration.compareTo(Duration.ZERO) <= 0) {
			throw new IllegalArgumentException("processingWindowDuration is not a positive duration!");
		}
		if (beforeProcessingWindowFetchDuration.isNegative()) {
			throw new IllegalArgumentException("beforeProcessingWindowFetchDuration is negative!");
		}
		if (afterProcessingWindowFetchDuration.isNegative()) {
			throw new IllegalArgumentException("afterProcessingWindowFetchDuration is negative!");
		}
		this.processingWindowDuration = processingWindowDuration;
		this.beforeProcessingWindowFetchDuration = beforeProcessingWindowFetchDuration;
		this.afterProcessingWindowFetchDuration = afterProcessingWindowFetchDuration;
	}

	private DataChunk fetchDataChunk(Instant dataStartInstant, Instant dataEndInstant) throws SolarThingDatabaseException {
		MillisQuery query = new MillisQueryBuilder()
				.startKey(dataStartInstant.toEpochMilli())
				.endKey(dataEndInstant.toEpochMilli())
				.inclusiveEnd(false)
				.build();

		// TODO This is a perfect spot for structured concurrency
		List<StoredPacketGroup> statusPackets = database.getStatusDatabase().query(query);
		List<StoredPacketGroup> eventPackets = database.getEventDatabase().query(query);
		return new DataChunk(statusPackets, eventPackets);
	}
	private DataChunk fetchDataChunkWithRetry(Instant dataStartInstant, Instant dataEndInstant) throws AnalysisException {
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				return fetchDataChunk(dataStartInstant, dataEndInstant);
			} catch (SolarThingDatabaseException e) {
				if (i == MAX_RETRIES - 1) {
					throw new AnalysisException("Reached max retries while querying from " + dataStartInstant + " to " + dataEndInstant, e);
				}
			}
		}
		throw new AssertionError("This should never happen");
	}

	public List<T> analyze(Instant startTime, Instant endTime) throws AnalysisException {
		List<T> combinedResults = new ArrayList<>();
		for (Instant processingStartInstant = startTime, processingEndInstant; processingStartInstant.compareTo(endTime) < 0; processingStartInstant = processingEndInstant) {
			processingEndInstant = processingStartInstant.plus(processingWindowDuration);
			if (processingEndInstant.isAfter(endTime)) {
				processingEndInstant = endTime;
			}
			Instant dataStartInstant = processingStartInstant.minus(beforeProcessingWindowFetchDuration);
			Instant dataEndInstant = processingEndInstant.plus(afterProcessingWindowFetchDuration);

			// TODO code after this comment in this for loop can be parallelized
			DataChunk dataChunk = fetchDataChunkWithRetry(processingStartInstant, dataEndInstant);
			List<? extends T> results = analyzer.analyze(dataStartInstant, processingStartInstant, processingEndInstant, dataEndInstant, dataChunk);
			combinedResults.addAll(results);
		}
		return combinedResults;
	}
}
