package me.retrodaredevil.solarthing.solcast;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@UtilityClass
public final class SolcastRetrofitUtil {
	private SolcastRetrofitUtil() { throw new UnsupportedOperationException(); }

	public static Retrofit.Builder defaultBuilder(){
		return defaultBuilder(new Retrofit.Builder(), "https://api.solcast.com.au/");
	}

	public static Retrofit.Builder defaultBuilder(Retrofit.Builder builder, String apiUrl){
		return builder.baseUrl(apiUrl)
				.addConverterFactory(ScalarsConverterFactory.create()) // we get a webpage response back, so we want to deserialize that to a regular String with no strings attached (pun intended)
				.addConverterFactory(JacksonConverterFactory.create(JacksonUtil.defaultMapper()))
				;
	}
}
