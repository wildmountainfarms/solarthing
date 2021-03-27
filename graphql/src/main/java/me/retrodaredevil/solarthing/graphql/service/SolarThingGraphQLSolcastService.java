package me.retrodaredevil.solarthing.graphql.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.graphql.solcast.SolcastConfig;
import me.retrodaredevil.solarthing.solcast.SolcastOkHttpUtil;
import me.retrodaredevil.solarthing.solcast.SolcastRetrofitUtil;
import me.retrodaredevil.solarthing.solcast.SolcastService;
import me.retrodaredevil.solarthing.solcast.common.Forecast;
import me.retrodaredevil.solarthing.solcast.common.SimpleEstimatedActual;
import me.retrodaredevil.solarthing.solcast.rooftop.EstimatedActualCache;
import me.retrodaredevil.solarthing.solcast.rooftop.EstimatedActualRetriever;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static me.retrodaredevil.solarthing.graphql.service.SchemaConstants.DESCRIPTION_FROM;
import static me.retrodaredevil.solarthing.graphql.service.SchemaConstants.DESCRIPTION_TO;

public class SolarThingGraphQLSolcastService {
	/*
	Inspiration for this: https://forums.solcast.com.au/t/using-the-api-with-node-red-and-influxdb-grafana/221
	Also discussed here: https://discourse.nodered.org/t/power-forecasts-and-data-tuning-for-solar-installations-node-red-solcast-api-influxdb-grafana/28037/2
	 */

	private final Map<String, SolcastHandler> sourceHandlerMap;
	private final ZoneId zoneId;

	public SolarThingGraphQLSolcastService(SolcastConfig solcastConfig, ZoneId zoneId) {
		this.zoneId = zoneId;
		sourceHandlerMap = new HashMap<>();
		for (String sourceId : solcastConfig.getSources()) {
			SolcastConfig.Entry entry = requireNonNull(solcastConfig.getEntry(sourceId));
			String apiKey = entry.getApiKey();
			OkHttpClient client = SolcastOkHttpUtil.configure(new OkHttpClient.Builder(), apiKey)
					.build();
			Retrofit retrofit = SolcastRetrofitUtil.defaultBuilder()
					.client(client)
					.build();
			SolcastService service = retrofit.create(SolcastService.class);
			sourceHandlerMap.put(sourceId, new SolcastHandler(
					service,
					new EstimatedActualCache(EstimatedActualRetriever.createRooftop(service, entry.getResourceId()))
			));
			System.out.println("For source: " + sourceId + " using solcast resource ID: " + entry.getResourceId());
		}
	}


	@GraphQLQuery
	public @Nullable SolarThingSolcastQuery querySolcast(
			@GraphQLArgument(name = "from", description = DESCRIPTION_FROM) long from, @GraphQLArgument(name = "to", description = DESCRIPTION_TO) long to,
			@GraphQLArgument(name = "sourceId") @NotNull String sourceId){
		SolcastHandler handler = sourceHandlerMap.get(sourceId);
		if (handler == null) {
			return null;
		}
		return new SolarThingSolcastQuery(handler, from, to, zoneId);
	}
	@GraphQLQuery
	public @Nullable SolarThingSolcastDayQuery querySolcastDay(
			@GraphQLArgument(name = "to", description = "Used to determine what day to query. Should be set similar to other 'to' arguments.") long to,
			@GraphQLArgument(name = "sourceId") @NotNull String sourceId){
		SolcastHandler handler = sourceHandlerMap.get(sourceId);
		if (handler == null) {
			return null;
		}
		return new SolarThingSolcastDayQuery(handler, to, zoneId);
	}
	public static class SolarThingSolcastQuery {
		private final SolcastHandler handler;
		private final long from;
		private final long to;
		private final ZoneId zoneId;

		public SolarThingSolcastQuery(SolcastHandler handler, long from, long to, ZoneId zoneId) {
			requireNonNull(this.handler = handler);
			this.from = from;
			this.to = to;
			requireNonNull(this.zoneId = zoneId);
		}

		@GraphQLQuery
		public @NotNull List<@NotNull SimpleEstimatedActual> queryEstimateActuals() throws IOException {
			return handler.cache.getEstimatedActuals(from, to, true);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull Forecast> queryForecasts(@GraphQLArgument(name = "includePast", defaultValue = "false") boolean includePast) throws IOException {
			long now = System.currentTimeMillis();
			if (!includePast && to < now) {
				return Collections.emptyList(); // they don't want past data, but their constants are for past data
			}
			long start = includePast ? from : Math.max(from, now);
			return handler.cache.getForecasts(start, to, true);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull DailyEnergy> queryDailyEnergyEstimates() throws IOException {
			LocalDate startDate = Instant.ofEpochMilli(from).atZone(zoneId).toLocalDate();
			LocalDate endDate = Instant.ofEpochMilli(to).atZone(zoneId).toLocalDate();
			List<SimpleEstimatedActual> simpleEstimatedActuals = handler.cache.getEstimatedActuals(
					startDate.atStartOfDay(zoneId).toInstant().toEpochMilli(),
					endDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli(),
					false
			);


			SimpleEstimatedActual first = simpleEstimatedActuals.get(0);

			startDate = max(startDate, first.getPeriodMidpoint().atZone(zoneId).toLocalDate().plusDays(1));
			SimpleEstimatedActual last = simpleEstimatedActuals.get(simpleEstimatedActuals.size() - 1);

			endDate = min(endDate, last.getPeriodMidpoint().atZone(zoneId).toLocalDate().minusDays(1));

			Map<LocalDate, Float> map = new HashMap<>();
			for (SimpleEstimatedActual estimatedActual : simpleEstimatedActuals) {
				LocalDate date = estimatedActual.getPeriodMidpoint().atZone(zoneId).toLocalDate();
				if (startDate.compareTo(date) > 0 || endDate.compareTo(date) < 0) {
					continue;
				}
				Float dailyKWH = map.get(date);
				if (dailyKWH == null) {
					dailyKWH = 0.0f;
				}
				dailyKWH += estimatedActual.getEnergyGenerationEstimate();
				map.put(date, dailyKWH);
			}
			Set<DailyEnergy> r = new TreeSet<>(Comparator.comparing(DailyEnergy::getDayStart));
			for (Map.Entry<LocalDate, Float> entry : map.entrySet()) {
				r.add(new DailyEnergy(entry.getKey().atStartOfDay(zoneId).toInstant().toEpochMilli(), entry.getValue()));
			}
			return new ArrayList<>(r);
		}
	}

	public static class SolarThingSolcastDayQuery {
		private final SolcastHandler handler;
		private final long to;
		private final ZoneId zoneId;

		public SolarThingSolcastDayQuery(SolcastHandler handler, long to, ZoneId zoneId) {
			requireNonNull(this.handler = handler);
			this.to = to;
			requireNonNull(this.zoneId = zoneId);
		}
		@GraphQLQuery(description = "Queries the kWh generation estimate for a certain day. offset of 0 is today, 1 is tomorrow, -1 is yesterday")
		public @NotNull DailyEnergy queryEnergyEstimate(@GraphQLArgument(name = "offset", defaultValue = "0") int offsetDays) throws IOException {
			LocalDate date = Instant.ofEpochMilli(to).atZone(zoneId).toLocalDate().plusDays(offsetDays);
			long start = date.atStartOfDay(zoneId).toInstant().toEpochMilli();
			long end = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1;
			List<SimpleEstimatedActual> estimatedActuals = handler.cache.getEstimatedActuals(start, end, true);
			if (estimatedActuals.isEmpty()) {
				throw new RuntimeException("Empty result for offset=" + offsetDays + "! This shouldn't happen!");
			}
			float dailyKWH = 0;
			for (SimpleEstimatedActual simpleEstimatedActual : estimatedActuals) {
				dailyKWH += simpleEstimatedActual.getEnergyGenerationEstimate();
			}
			return new DailyEnergy(start, dailyKWH);
		}
	}

	private static class SolcastHandler {
		private final SolcastService service;
		private final EstimatedActualCache cache;

		private SolcastHandler(SolcastService service, EstimatedActualCache cache) {
			this.service = service;
			this.cache = cache;
		}
	}

	public static class DailyEnergy {
		private final long dayStart;
		private final float dailyKWH;

		public DailyEnergy(long dayStart, float dailyKWH) {
			this.dayStart = dayStart;
			this.dailyKWH = dailyKWH;
		}

		@JsonProperty("dayStart")
		public long getDayStart() {
			return dayStart;
		}

		@JsonProperty("dailyKWH")
		public float getDailyKWH() {
			return dailyKWH;
		}
	}
	private static <T extends Comparable<? super T>> T min(T a, T b) {
		if (a.compareTo(b) < 0) {
			return a;
		}
		return b;
	}
	private static <T extends Comparable<? super T>> T max(T a, T b) {
		if (a.compareTo(b) < 0) {
			return b;
		}
		return a;
	}
}
