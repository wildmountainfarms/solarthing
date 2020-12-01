package me.retrodaredevil.solarthing.graphql.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import jdk.dynalink.linker.ConversionComparator;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.graphql.solcast.SolcastConfig;
import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.solcast.SolcastOkHttpUtil;
import me.retrodaredevil.solarthing.solcast.SolcastRetrofitUtil;
import me.retrodaredevil.solarthing.solcast.SolcastService;
import me.retrodaredevil.solarthing.solcast.common.EstimatedActual;
import me.retrodaredevil.solarthing.solcast.common.Forecast;
import me.retrodaredevil.solarthing.solcast.common.IntervalData;
import me.retrodaredevil.solarthing.solcast.common.SimpleEstimatedActual;
import me.retrodaredevil.solarthing.solcast.rooftop.EstimatedActualCache;
import me.retrodaredevil.solarthing.solcast.rooftop.EstimatedActualRetriever;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class SolarThingGraphQLSolcastService {
	/*
	Inspiration for this: https://forums.solcast.com.au/t/using-the-api-with-node-red-and-influxdb-grafana/221
	Also discussed here: https://discourse.nodered.org/t/power-forecasts-and-data-tuning-for-solar-installations-node-red-solcast-api-influxdb-grafana/28037/2
	 */

	private final Map<String, SolcastHandler> sourceHandlerMap;
	private final TimeZone timeZone;

	public SolarThingGraphQLSolcastService(SolcastConfig solcastConfig, TimeZone timeZone) {
		this.timeZone = timeZone;
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
	public SolarThingSolcastQuery querySolcast(
			@GraphQLArgument(name = "from") long from, @GraphQLArgument(name = "to") long to,
			@GraphQLArgument(name = "sourceId") @Nullable String sourceId){
		SolcastHandler handler = sourceHandlerMap.get(sourceId);
		return new SolarThingSolcastQuery(handler, from, to, timeZone);
	}
	public static class SolarThingSolcastQuery {
		private final @Nullable SolcastHandler handler;
		private final long from;
		private final long to;
		private final TimeZone timeZone;

		public SolarThingSolcastQuery(@Nullable SolcastHandler handler, long from, long to, TimeZone timeZone) {
			this.handler = handler;
			this.from = from;
			this.to = to;
			this.timeZone = timeZone;
		}

		@GraphQLQuery
		public List<SimpleEstimatedActual> queryEstimateActuals() throws IOException {
			if (handler == null) {
				return Collections.emptyList();
			}
			return handler.cache.getEstimatedActuals(from, to);
		}
		@GraphQLQuery
		public List<Forecast> queryForecasts(@GraphQLArgument(name = "includePast", defaultValue = "false") boolean includePast) throws IOException {
			if (handler == null) {
				return Collections.emptyList();
			}
			long now = System.currentTimeMillis();
			if (!includePast && to < now) {
				return Collections.emptyList(); // they don't want past data, but their constants are for past data
			}
			long start = includePast ? from : Math.max(from, now);
			return handler.cache.getForecasts(start, to);
		}
		@GraphQLQuery
		public @NotNull List<@NotNull DailyEnergy> queryDailyEnergyEstimates() throws IOException {
			if (handler == null) {
				return Collections.emptyList();
			}
			List<SimpleEstimatedActual> simpleEstimatedActuals = handler.cache.getEstimatedActuals(from, to);

			SimpleDate startDate = SimpleDate.fromDateMillis(from, timeZone);
			SimpleDate endDate = SimpleDate.fromDateMillis(to, timeZone).tomorrow();

			SimpleEstimatedActual first = simpleEstimatedActuals.get(0);
			startDate = max(startDate, SimpleDate.fromDateMillis(first.getPeriodMidpoint().toEpochMilli(), timeZone).tomorrow());
			SimpleEstimatedActual last = simpleEstimatedActuals.get(simpleEstimatedActuals.size() - 1);
			endDate = min(endDate, SimpleDate.fromDateMillis(last.getPeriodMidpoint().toEpochMilli(), timeZone).yesterday());

			Map<SimpleDate, Float> map = new HashMap<>();
			for (SimpleEstimatedActual estimatedActual : simpleEstimatedActuals) {
				SimpleDate date = SimpleDate.fromDateMillis(estimatedActual.getPeriodMidpoint().toEpochMilli(), timeZone);
				if (startDate.compareTo(date) > 0 || endDate.compareTo(date) < 0) {
					continue;
				}
				Float dailyKWH = map.get(date);
				if (dailyKWH == null) {
					dailyKWH = 0.0f;
				}
				dailyKWH += estimatedActual.getPVEstimate() * estimatedActual.getPeriod().toMinutes() / 60.0f;
				map.put(date, dailyKWH);
			}
			Set<DailyEnergy> r = new TreeSet<>(Comparator.comparing(DailyEnergy::getDayStart));
			for (Map.Entry<SimpleDate, Float> entry : map.entrySet()) {
				r.add(new DailyEnergy(entry.getKey().getDayStartDateMillis(timeZone), entry.getValue()));
			}
			return new ArrayList<>(r);
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
	private static <T extends Comparable<T>> T max(T a, T b) {
		if (a.compareTo(b) < 0) {
			return b;
		}
		return a;
	}
	private static <T extends Comparable<T>> T min(T a, T b) {
		if (a.compareTo(b) < 0) {
			return a;
		}
		return b;
	}
}
