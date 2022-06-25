package me.retrodaredevil.solarthing.solcast.rooftop;

import me.retrodaredevil.solarthing.solcast.common.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class EstimatedActualCache {
	private static final Logger LOGGER = LoggerFactory.getLogger(EstimatedActualCache.class);
	private static final Duration DURATION_7_DAYS = Duration.ofDays(7);
	private static final Duration REQUEST_PERIOD = Duration.ofHours(6);

	private final TreeSet<Node> simpleEstimatedActualSet = new TreeSet<>();
	private final TreeSet<Node> forecastSet = new TreeSet<>();

	private final EstimatedActualRetriever retriever;

	private Long lastRequest = null;
	private boolean freeToRequestPast = false;

	public EstimatedActualCache(EstimatedActualRetriever retriever) {
		this.retriever = retriever;
	}

	/**
	 * Note: If {@code startPointMillis} is before now, a request to Solcast for past estimated actuals will be made.
	 * Many times this is not ideal as you already know what you got, so there's usually little reason to request that data.
	 *
	 * @param startPointMillis Helps determine whether data needs to be requested
	 * @param endPointMillis Helps determine whether data needs to be requested
	 * @param clamp If true, returned data will only be between {@code startPointMillis} and {@code endPointMillis}. If false, all data will be returned
	 * @return A list of {@link SimpleEstimatedActual}s. Ordered from oldest to newest
	 * @throws IOException thrown if there's an error while making a request
	 */
	public List<SimpleEstimatedActual> getEstimatedActuals(long startPointMillis, long endPointMillis, boolean clamp) throws IOException {
		if (startPointMillis > endPointMillis) {
			throw new IllegalArgumentException("start > end!! startPointMillis: " + startPointMillis + " endPointMillis: " + endPointMillis);
		}
		long now = System.currentTimeMillis();
		if (now - DURATION_7_DAYS.toMillis() > endPointMillis) { // we can't get data more than 7 days in the past
			return Collections.emptyList();
		}
		if (now + DURATION_7_DAYS.toMillis() < startPointMillis) { // we can't get data more than 7 days in the future
			return Collections.emptyList();
		}

		synchronized (this) {
			boolean needsPast = startPointMillis + 5 * 60 * 1000 < now; // we need past data if the start point is more than 5 minutes in the past
			if (isTimestampOld(now, lastRequest)) {
				boolean newFreeToRequestPast = true;
				if (needsPast) { // only request past data if the start point is in the past
					updatePastEstimatedActuals();
					newFreeToRequestPast = false;
				}
				updateFutureForecastsAndEstimatedActuals();
				lastRequest = now;
				freeToRequestPast = newFreeToRequestPast;
			} else if (freeToRequestPast) {
				if (needsPast) {
					freeToRequestPast = false;
					updatePastEstimatedActuals();
				}
			}
			List<SimpleEstimatedActual> r = new ArrayList<>();
			for (Node node : clamp ? simpleEstimatedActualSet.tailSet(new Node(startPointMillis), true).headSet(new Node(endPointMillis), true) : simpleEstimatedActualSet) {
				r.add((SimpleEstimatedActual) node.data);
			}
			return Collections.unmodifiableList(r);
		}
	}

	public List<Forecast> getForecasts(long startPointMillis, long endPointMillis, boolean clamp) throws IOException {
		long now = System.currentTimeMillis();
		if (now + DURATION_7_DAYS.toMillis() < startPointMillis) { // we can't get data more than 7 days in the future
			return Collections.emptyList();
		}
		if (now > endPointMillis) { // we can't get past forecast data
			return Collections.emptyList();
		}

		synchronized (this) {
			if (isTimestampOld(now, lastRequest)) {
				updateFutureForecastsAndEstimatedActuals();
				lastRequest = now;
				freeToRequestPast = true;
			}

			List<Forecast> r = new ArrayList<>();
			for (Node node : clamp ? forecastSet.tailSet(new Node(startPointMillis), true).headSet(new Node(endPointMillis), true) : forecastSet) {
				r.add((Forecast) node.data);
			}
			return Collections.unmodifiableList(r);
		}
	}
	private static boolean isTimestampOld(long now, Long timestamp) {
		return timestamp == null || timestamp + REQUEST_PERIOD.toMillis() < now;
	}
	private void updateFutureForecastsAndEstimatedActuals() throws IOException {
		LOGGER.info("Going to retrieve forecasts from solcast!");
		ForecastResult result = retriever.retrieveForecast();
		List<Forecast> forecasts = result.getForecasts();
		List<Node> nodes = new ArrayList<>();
		for (Forecast forecast : forecasts) {
			nodes.add(new Node(forecast));
		}
		forecastSet.tailSet(nodes.get(0), true).clear();
		forecastSet.addAll(nodes);

		simpleEstimatedActualSet.tailSet(nodes.get(0), true).clear();
		simpleEstimatedActualSet.addAll(nodes);
	}
	private void updatePastEstimatedActuals() throws IOException {
		LOGGER.warn("Going to retrieve PAST estimated actuals from solcast! This is not ideal. Using a cache for past data is recommended instead.");
		EstimatedActualResult result = retriever.retrievePast();
		List<EstimatedActual> estimatedActuals = result.getEstimatedActuals();
		List<Node> nodes = new ArrayList<>();
		for (EstimatedActual estimatedActual : estimatedActuals) {
			nodes.add(new Node(estimatedActual));
		}

		simpleEstimatedActualSet.headSet(nodes.get(nodes.size() - 1), true).clear();
		simpleEstimatedActualSet.addAll(nodes);
	}

	private static class Node implements Comparable<Node> {
		private final long dateMillis;
		private final IntervalData data;

		private Node(IntervalData data) {
			dateMillis = data.getPeriodEnd().toEpochMilli();
			this.data = data;
		}
		private Node(long dateMillis) {
			this.dateMillis = dateMillis;
			data = null;
		}

		@Override
		public int compareTo(@NotNull EstimatedActualCache.Node node) {
			return Long.compare(dateMillis, node.dateMillis);
		}
	}
}
