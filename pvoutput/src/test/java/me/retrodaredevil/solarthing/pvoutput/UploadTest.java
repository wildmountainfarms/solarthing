package me.retrodaredevil.solarthing.pvoutput;

import me.retrodaredevil.solarthing.pvoutput.data.AddOutputParamtersBuilder;
import me.retrodaredevil.solarthing.pvoutput.service.OkHttpUtil;
import me.retrodaredevil.solarthing.pvoutput.service.PVOutputService;
import me.retrodaredevil.solarthing.pvoutput.service.RetrofitUtil;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

public class UploadTest {
//	private static void testMine(OkHttpClient client) throws PVOutputException {
////		/*
//		new OkHttpPVOutputExporter(client).exportOutput(new OutputServiceDataBuilder(new SimpleDate(2019, 12, 16))
//				.setGenerated(4200)
//				.setComments("Simple test with real data.")
//				.build()
//		);
//	}
	private static void testRetrofit(OkHttpClient client) throws IOException {
		Retrofit retrofit = RetrofitUtil.defaultBuilder()
				.client(client)
				.build();
		PVOutputService service = retrofit.create(PVOutputService.class);
		Call<String> call = service.addOutput(
				new AddOutputParamtersBuilder(new SimpleDate(2020, 1, 30))
						.setGenerated(7000)
						.build()
		);
		Response<String> response = call.execute();
		System.out.println(response);
	}
	public static void main(String[] args) throws PVOutputException, IOException {
		OkHttpClient client = OkHttpUtil.configure(new OkHttpClient.Builder(), "", 72206)
				.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY))
				.build();
		testRetrofit(client);
	}
}
