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
	private static final Duration DURATION_6_DAYS = Duration.ofDays(6);
	private static final Duration DURATION_7_DAYS = Duration.ofDays(7);

	private final TreeSet<Node> simpleEstimatedActualSet = new TreeSet<>();
	private final TreeSet<Node> forecastSet = new TreeSet<>();

	private final EstimatedActualRetriever retriever;

	public EstimatedActualCache(EstimatedActualRetriever retriever) {
		this.retriever = retriever;
	}
	public List<SimpleEstimatedActual> getEstimatedActuals(long startPointMillis, long endPointMillis) throws IOException {
//		List<Forecast> forecasts = getForecasts(startPointMillis, endPointMillis);
//		if (!forecastSet.headSet(new Node(startPointMillis), true).isEmpty()) { // we're only asking for future results, so we can just use the getForecasts() method
//			return new ArrayList<>(forecasts);
//		}
		long now = System.currentTimeMillis();
		if (now - DURATION_7_DAYS.toMillis() > endPointMillis) {
			return Collections.emptyList();
		}
		if (now + DURATION_7_DAYS.toMillis() < startPointMillis) {
			return Collections.emptyList();
		}
		if (simpleEstimatedActualSet.isEmpty()) {
			if (endPointMillis >= now - 1000 * 60 * 60 * 24 && forecastSet.isEmpty()) {
				// if we want data less than a day old, it's beneficial to use to get some future data in case we want past data again (the future data will eventually be past data)
				updateForecasts();
			}
			updateEstimatedActuals();
		}
		List<SimpleEstimatedActual> r = new ArrayList<>();
		for (Node node : simpleEstimatedActualSet.tailSet(new Node(startPointMillis), true).headSet(new Node(endPointMillis), true)) {
			r.add((SimpleEstimatedActual) node.data);
		}
		return r;
	}

	public List<Forecast> getForecasts(long startPointMillis, long endPointMillis) throws IOException {
		long now = System.currentTimeMillis();
		if (now + DURATION_7_DAYS.toMillis() < startPointMillis) {
			return Collections.emptyList();
		}
		if (!satisfiesEndpoint(forecastSet, endPointMillis)) {
			updateForecasts();
		}
		List<Forecast> r = new ArrayList<>();
		for (Node node : forecastSet.tailSet(new Node(startPointMillis), true).headSet(new Node(endPointMillis), true)) {
			r.add((Forecast) node.data);
		}
		return r;
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


	private static boolean satisfiesEndpoint(TreeSet<Node> set, long endPointMillis) {
		if(!set.tailSet(new Node(endPointMillis), true).isEmpty()) { // there's data after the endpoint
			return true;
		}
		if (set.isEmpty()) {
			return false;
		}
		long endingMillis = set.last().dateMillis;
		long now = System.currentTimeMillis();
		return now + DURATION_6_DAYS.toMillis() <= endingMillis; // It satisfies if the ending millis is further than 6 days in the future
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
