package me.retrodaredevil.solarthing.solcast.rooftop;

import me.retrodaredevil.solarthing.solcast.common.Forecast;
import me.retrodaredevil.solarthing.solcast.common.ForecastResult;
import me.retrodaredevil.solarthing.solcast.common.IntervalData;
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

//	private final TreeSet<Node> sortedSet = new TreeSet<>();
	private final TreeSet<Node> forecastSet = new TreeSet<>();

	private final EstimatedActualRetriever retriever;

	public EstimatedActualCache(EstimatedActualRetriever retriever) {
		this.retriever = retriever;
	}
	// TODO estimated actuals stuff

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
		System.out.println("Going to retrieve forecasts from solcast!"); // TODO remove this debug message later
		ForecastResult result = retriever.retrieveForecast();
		List<Forecast> forecasts = result.getForecasts();
		List<Node> nodes = new ArrayList<>();
		for (Forecast forecast : forecasts) {
			nodes.add(new Node(forecast));
		}
		forecastSet.tailSet(nodes.get(0), true).clear();
		forecastSet.addAll(nodes);
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
