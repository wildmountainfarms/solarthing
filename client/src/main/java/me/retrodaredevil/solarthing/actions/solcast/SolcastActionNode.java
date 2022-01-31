package me.retrodaredevil.solarthing.actions.solcast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestFragmentedPacketGroupEnvironment;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.PowerUtil;
import me.retrodaredevil.solarthing.solcast.*;
import me.retrodaredevil.solarthing.solcast.rooftop.Measurement;
import me.retrodaredevil.solarthing.solcast.rooftop.MeasurementData;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@JsonTypeName("solcast")
public class SolcastActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolcastActionNode.class);

	private final String resourceId;
	private final SolcastService service;

	private final ExecutorService executorService = Executors.newFixedThreadPool(3);

	@JsonCreator
	public SolcastActionNode(@JsonProperty(value = "resource", required = true) String resourceId, @JsonProperty(value = "key", required = true) String apiKey) {
		this.resourceId = resourceId;

		OkHttpClient client = SolcastOkHttpUtil.configure(new OkHttpClient.Builder(), apiKey)
//				.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY))
				.build();
		Retrofit retrofit = SolcastRetrofitUtil.defaultBuilder()
				.client(client)
				.build();
		service = retrofit.create(SolcastService.class);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		LatestFragmentedPacketGroupEnvironment latestPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestFragmentedPacketGroupEnvironment.class);

		return Actions.createRunOnce(() -> {
			FragmentedPacketGroup packetGroup = latestPacketGroupEnvironment.getFragmentedPacketGroupProvider().getPacketGroup();
			if (packetGroup == null) {
				LOGGER.warn("packetGroup is null! Not uploading anything to solcast.");
				return;
			}
			PowerUtil.Data data = PowerUtil.getPowerData(packetGroup, PowerUtil.GeneratingType.PV_ONLY);
			Integer watts = data.getGeneratingWatts();
			if (watts != null) {
				LOGGER.debug("Total " + watts + " watts will be uploaded to Solcast");
				Call<?> call = service.postMeasurement(resourceId, MeasurementData.createSingle(
						new Measurement(
								Instant.ofEpochMilli(packetGroup.getDateMillis()),
								Duration.ofMinutes(5),
								watts / 1000.0f
						)
				));
				executorService.execute(() -> {
					try {
						Response<?> response = call.execute();
						if (!response.isSuccessful()) {
							LOGGER.error("Unsuccessful solcast response! Code: " + response.code());
						}
					} catch (IOException e) {
						LOGGER.error("Solcast error", e);
					}
				});
			}
		});
	}
}
