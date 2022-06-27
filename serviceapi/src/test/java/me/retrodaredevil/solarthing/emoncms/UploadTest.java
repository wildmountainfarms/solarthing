package me.retrodaredevil.solarthing.emoncms;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

public class UploadTest {
	private UploadTest() { throw new UnsupportedOperationException(); }
	private static void testRetrofit(OkHttpClient client) throws IOException {
		Retrofit retrofit = EmonCmsRetrofitUtil.defaultBuilder()
				.client(client)
				.build();
		EmonCmsService service = retrofit.create(EmonCmsService.class);
		Call<String> call = service.input("Fragment 1", new InputData.Builder()
				.add("use", 240)
				.add("solar", 200)
				.add("use_kwh", 6.5)
				.add("solar_kwh", 11.9)
				.add("import_kwh", 2.9)
				.build(), System.currentTimeMillis() / 1000);
		Response<String> response = call.execute();
		System.out.println(response);
	}
	public static void main(String[] args) throws IOException {
		OkHttpClient client = EmonCmsOkHttpUtil.configure(new OkHttpClient.Builder(), "<replace me>")
				.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY))
				.build();
		testRetrofit(client);
	}
}
