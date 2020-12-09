package me.retrodaredevil.solarthing.solcast.rooftop;

import me.retrodaredevil.solarthing.solcast.common.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class EstimatedActualCache {
	private static final Duration DURATION_7_DAYS = Duration.ofDays(7);

	private final TreeSet<Node> simpleEstimatedActualSet = new TreeSet<>();
	private final TreeSet<Node> forecastSet = new TreeSet<>();

	private final EstimatedActualRetriever retriever;

	private Long lastRequest = null;
	private boolean freeToRequestPast = false;

	public EstimatedActualCache(EstimatedActualRetriever retriever) {
		this.retriever = retriever;
	}

	/**
	 *
	 * @param startPointMillis Helps determine whether data needs to be requested
	 * @param endPointMillis Helps determine whether data needs to be requested
	 * @return A list of {@link SimpleEstimatedActual}s. Ordered from oldest to newest
	 * @throws IOException thrown if there's an error while making a request
	 */
	public List<SimpleEstimatedActual> getEstimatedActuals(long startPointMillis, long endPointMillis, boolean clamp) throws IOException {
		long now = System.currentTimeMillis();
		if (now - DURATION_7_DAYS.toMillis() > endPointMillis) {
			return Collections.emptyList();
		}
		if (now + DURATION_7_DAYS.toMillis() < startPointMillis) {
			return Collections.emptyList();
		}

		if (isTimestampOld(now, lastRequest)) {
			lastRequest = now;
			freeToRequestPast = false;
			updateEstimatedActuals();
			updateForecasts();
		} else if (freeToRequestPast) {
			freeToRequestPast = false;
			updateEstimatedActuals();
		}

		List<SimpleEstimatedActual> r = new ArrayList<>();
		for (Node node : clamp ? simpleEstimatedActualSet.tailSet(new Node(startPointMillis), true).headSet(new Node(endPointMillis), true) : simpleEstimatedActualSet) {
			r.add((SimpleEstimatedActual) node.data);
		}
		return r;
	}

	public List<Forecast> getForecasts(long startPointMillis, long endPointMillis, boolean clamp) throws IOException {
		long now = System.currentTimeMillis();
		if (now + DURATION_7_DAYS.toMillis() < startPointMillis) {
			return Collections.emptyList();
		}

		if (isTimestampOld(now, lastRequest)) {
			lastRequest = now;
			freeToRequestPast = true;
			updateForecasts();
		}

		List<Forecast> r = new ArrayList<>();
		for (Node node : clamp ? forecastSet.tailSet(new Node(startPointMillis), true).headSet(new Node(endPointMillis), true) : forecastSet) {
			r.add((Forecast) node.data);
		}
		return r;
	}
	private static boolean isTimestampOld(long now, Long timestamp) {
		return timestamp == null || timestamp + 1000 * 60 * 60 * 12 < now;
	}
	private void updateForecasts() throws IOException {
		System.out.println("Going to retrieve forecasts from solcast!");
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
	private void updateEstimatedActuals() throws IOException {
		System.out.println("Going to retrieve estimated actuals from solcast!");
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
			return (int) (dateMillis - node.dateMillis);
		}
	}
}
