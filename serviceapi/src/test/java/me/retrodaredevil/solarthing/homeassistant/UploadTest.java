package me.retrodaredevil.solarthing.homeassistant;

import me.retrodaredevil.solarthing.homeassistant.data.SensorInfo;
import me.retrodaredevil.solarthing.homeassistant.data.SensorState;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadTest {
	private UploadTest() { throw new UnsupportedOperationException(); }

	private static void testRetrofit(OkHttpClient client) throws IOException {
		Retrofit retrofit = HomeAssistantRetrofitUtil.defaultBuilder(new Retrofit.Builder(), "http://192.168.1.210:8123/api/")
				.client(client)
				.build();
		HomeAssistantService service = retrofit.create(HomeAssistantService.class);
		Map<String, String> attributes = new HashMap<>();
		attributes.put("friendly_name", "Cool Sensor"); // this is required
		Call<SensorInfo> call = service.updateSensor("solarthing_coolsensor", new SensorState("Charging", attributes));
		Response<SensorInfo> result = call.execute();
		System.out.println(result);
		System.out.println(result.body());
	}
	public static void main(String[] args) throws IOException {
		// lmao yeah don't worry this token doesn't work anymore
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI3N2VhNzc4MTZkYTE0ZWEwYTY0MDljM2FkNmRiNGQ0MyIsImlhdCI6MTYwNjQ0NTA1OSwiZXhwIjoxOTIxODA1MDU5fQ.x3zQeH3JkybXx-y1Kn0KvSxoDCVXINyI6GqWc6yNmMQ";
		OkHttpClient client = HomeAssistantOkHttpUtil.configure(new OkHttpClient.Builder(), token)
				.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY))
				.build();
		testRetrofit(client);
	}

}
