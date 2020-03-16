package me.retrodaredevil.solarthing.emoncms;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class EmonCmsRetrofitUtil {
	private EmonCmsRetrofitUtil(){ throw new UnsupportedOperationException(); }


	public static Retrofit.Builder defaultBuilder(){
		return defaultBuilder(new Retrofit.Builder());
	}
	public static Retrofit.Builder defaultBuilder(Retrofit.Builder builder){
		return builder.baseUrl("https://emoncms.org/")
				.addConverterFactory(ScalarsConverterFactory.create())
				;
	}
}
