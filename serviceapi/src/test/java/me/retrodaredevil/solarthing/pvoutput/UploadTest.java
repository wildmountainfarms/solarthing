package me.retrodaredevil.solarthing.pvoutput;

import me.retrodaredevil.solarthing.pvoutput.data.AddOutputParametersBuilder;
import me.retrodaredevil.solarthing.pvoutput.service.PVOutputOkHttpUtil;
import me.retrodaredevil.solarthing.pvoutput.service.PVOutputService;
import me.retrodaredevil.solarthing.pvoutput.service.PVOutputRetrofitUtil;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

public class UploadTest {
	private static void testRetrofit(OkHttpClient client) throws IOException {
		Retrofit retrofit = PVOutputRetrofitUtil.defaultBuilder()
				.client(client)
				.build();
		PVOutputService service = retrofit.create(PVOutputService.class);
		Call<String> call = service.addOutput(
				new AddOutputParametersBuilder(new SimpleDate(2020, 1, 30))
						.setGenerated(7000)
						.build()
		);
		Response<String> response = call.execute();
		System.out.println(response);
	}
	public static void main(String[] args) throws IOException {
		OkHttpClient client = PVOutputOkHttpUtil.configure(new OkHttpClient.Builder(), "", 72206)
				.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY))
				.build();
		testRetrofit(client);
	}
}
