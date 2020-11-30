package me.retrodaredevil.solarthing.graphql.service;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.graphql.packets.DataNode;
import me.retrodaredevil.solarthing.graphql.solcast.SolcastConfig;
import me.retrodaredevil.solarthing.solcast.SolcastOkHttpUtil;
import me.retrodaredevil.solarthing.solcast.SolcastRetrofitUtil;
import me.retrodaredevil.solarthing.solcast.SolcastService;
import me.retrodaredevil.solarthing.solcast.common.Forecast;
import me.retrodaredevil.solarthing.solcast.rooftop.EstimatedActualCache;
import me.retrodaredevil.solarthing.solcast.rooftop.EstimatedActualRetriever;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class SolarThingGraphQLSolcastService {

	private final Map<String, SolcastHandler> sourceHandlerMap;

	public SolarThingGraphQLSolcastService(SolcastConfig solcastConfig) {
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
		return new SolarThingSolcastQuery(handler, from, to);
	}
	public static class SolarThingSolcastQuery {
		private final @Nullable SolcastHandler handler;
		private final long from;
		private final long to;

		public SolarThingSolcastQuery(@Nullable SolcastHandler handler, long from, long to) {
			this.handler = handler;
			this.from = from;
			this.to = to;
		}

//		public List<DataNode<Float>> queryEstimateActuals() {
//
//		}
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
	}
	private static class SolcastHandler {
		private final SolcastService service;
		private final EstimatedActualCache cache;

		private SolcastHandler(SolcastService service, EstimatedActualCache cache) {
			this.service = service;
			this.cache = cache;
		}
	}
}
