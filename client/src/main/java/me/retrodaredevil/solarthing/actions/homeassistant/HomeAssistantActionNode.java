package me.retrodaredevil.solarthing.actions.homeassistant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestFragmentedPacketGroupEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.homeassistant.HomeAssistantOkHttpUtil;
import me.retrodaredevil.solarthing.homeassistant.HomeAssistantRetrofitUtil;
import me.retrodaredevil.solarthing.homeassistant.HomeAssistantService;
import me.retrodaredevil.solarthing.homeassistant.data.AttributeBuilder;
import me.retrodaredevil.solarthing.homeassistant.data.BinarySensorState;
import me.retrodaredevil.solarthing.homeassistant.data.SensorInfo;
import me.retrodaredevil.solarthing.homeassistant.data.SensorState;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@JsonTypeName("homeassistant")
public class HomeAssistantActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeAssistantActionNode.class);
	private static final NumberFormat FORMAT = new DecimalFormat("0.0");

	private final HomeAssistantService service;

	@JsonCreator
	public HomeAssistantActionNode(@JsonProperty("token") String token, @JsonProperty("url") String url) {
		OkHttpClient client = HomeAssistantOkHttpUtil.configure(new OkHttpClient.Builder(), token)
//				.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY))
				.build();
		if (!url.endsWith("/")) {
			url += "/";
		}
		if (!url.endsWith("api/")) {
			url += "api/";
		}
		Retrofit retrofit = HomeAssistantRetrofitUtil.defaultBuilder(new Retrofit.Builder(), url)
				.client(client)
				.build();
		service = retrofit.create(HomeAssistantService.class);
	}


	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		LatestFragmentedPacketGroupEnvironment latestFragmentedPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestFragmentedPacketGroupEnvironment.class);

		return Actions.createRunOnce(() -> {
			FragmentedPacketGroup packetGroup = latestFragmentedPacketGroupEnvironment.getFragmentedPacketGroupProvider().getPacketGroup();
			if (packetGroup == null) {
				LOGGER.warn("packetGroup is null!");
				return;
			}
			List<Call<SensorInfo>> calls = new ArrayList<>();
			for (Packet packet : packetGroup.getPackets()) {
				int fragmentId = packetGroup.getFragmentId(packet);
				String sourceId = packetGroup.getSourceId(packet);
				String nameBase = "solarthing_" + sourceId + "_" + fragmentId + "_";
				if (packet instanceof FXStatusPacket) {
					FXStatusPacket fx = (FXStatusPacket) packet;
					nameBase += "fx_" + fx.getAddress() + "_";
					calls.add(service.updateSensor(
							nameBase + "acmode",
							new SensorState(fx.getACMode().getModeName(), new AttributeBuilder().friendlyName("AC Mode").build())
					));
					calls.add(service.updateBinarySensor(
							nameBase + "acpresent",
							new BinarySensorState(fx.getACMode() != ACMode.NO_AC, new AttributeBuilder().friendlyName("AC Present").build())
					));
					calls.add(service.updateSensor(
							nameBase + "batteryvoltage",
							new SensorState(FORMAT.format(fx.getBatteryVoltage()), new AttributeBuilder().friendlyName("Battery Voltage").build())
					));
				}
			}
			for(Call<SensorInfo> call : calls) {
				try {
					// TODO move into a separate thread
					Response<SensorInfo> response = call.execute();
					if (!response.isSuccessful()) {
						LOGGER.error("Unsuccessful response! " + requireNonNull(response.errorBody()).string());
					}
				} catch (IOException e) {
					LOGGER.error("Home assistant error", e);
				}
			}
		});
	}
}
