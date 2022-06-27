package me.retrodaredevil.solarthing.solcast;

import me.retrodaredevil.solarthing.solcast.common.EstimatedActualResult;
import me.retrodaredevil.solarthing.solcast.common.ForecastResult;
import me.retrodaredevil.solarthing.solcast.rooftop.*;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static java.util.Objects.requireNonNull;

public class SolcastUploadTest {
	private SolcastUploadTest() { throw new UnsupportedOperationException(); }
	private static final String RESOURCE_ID = "90d5-ccbe-7af4-280c";
	private static void testRetrofit(OkHttpClient client) throws IOException {
		Retrofit retrofit = SolcastRetrofitUtil.defaultBuilder()
				.client(client)
				.build();
		SolcastService service = retrofit.create(SolcastService.class);
//		testPostMeasurement(service);
//		testForecasts(service);
		testEstimatedActuals(service);
	}
	private static void testPostMeasurement(SolcastService service) throws IOException{
		Call<?> call = service.postMeasurement(
				RESOURCE_ID,
				MeasurementData.createSingle(new Measurement(Instant.now(), Duration.ofMinutes(5), 0))
		);
		Response<?> response = call.execute();
		System.out.println(response);
	}
	private static void testForecasts(SolcastService service) throws IOException {
		Call<ForecastResult> call = service.retrieveForecasts(RESOURCE_ID, 24 * 3);
		Response<ForecastResult> response = call.execute();
		if (response.isSuccessful()) {
			ForecastResult result = requireNonNull(response.body());
			System.out.println("Got " + result.getForecasts().size() + " results");
		}
	}
	private static void testEstimatedActuals(SolcastService service) throws IOException {
		Call<EstimatedActualResult> call = service.retrieveEstimatedActuals(RESOURCE_ID, 24 * 3);
		Response<EstimatedActualResult> response = call.execute();
		if (response.isSuccessful()) {
			EstimatedActualResult result = requireNonNull(response.body());
			System.out.println("Got " + result.getEstimatedActuals().size() + " results");
		}
	}
	public static void main(String[] args) throws IOException {
		String apiKey = ""; // remember to remove this when you're done playing before committing!
		OkHttpClient client = SolcastOkHttpUtil.configure(new OkHttpClient.Builder(), apiKey)
				.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY))
				.build();
		testRetrofit(client);
	}
}
