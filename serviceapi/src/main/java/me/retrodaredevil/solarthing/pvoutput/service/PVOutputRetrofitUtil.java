package me.retrodaredevil.solarthing.pvoutput.service;

import me.retrodaredevil.solarthing.util.JacksonUtil;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class PVOutputRetrofitUtil {
	private PVOutputRetrofitUtil(){ throw new UnsupportedOperationException(); }

	public static Retrofit.Builder defaultBuilder(){
		return defaultBuilder(new Retrofit.Builder());
	}
	public static Retrofit.Builder defaultBuilder(Retrofit.Builder builder){
		return builder.baseUrl("https://pvoutput.org/service/r2/")
				.addConverterFactory(new FormBodyJacksonConverterFactory(JacksonUtil.defaultMapper()))
				.addConverterFactory(ScalarsConverterFactory.create());
	}
}
