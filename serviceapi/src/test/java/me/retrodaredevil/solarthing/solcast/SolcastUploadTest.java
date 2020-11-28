package me.retrodaredevil.solarthing.solcast;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class SolcastUploadTest {
	private static void testRetrofit(OkHttpClient client) throws IOException {
		Retrofit retrofit = SolcastRetrofitUtil.defaultBuilder()
				.client(client)
				.build();
		SolcastService service = retrofit.create(SolcastService.class);
		Call<String> call = service.postMeasurement(
				"",
				MeasurementData.createSingle(new Measurement(Instant.now(), Duration.ofMinutes(5), .0001f))
		);
		Response<String> response = call.execute();
		System.out.println(response);
	}
	public static void main(String[] args) throws IOException {
		String apiKey = ""; // remember to remove this when you're done playing before committing!
		OkHttpClient client = SolcastOkHttpUtil.configure(new OkHttpClient.Builder(), apiKey)
				.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY))
				.build();
		testRetrofit(client);
	}
}
