package me.retrodaredevil.solarthing.homeassistant;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@UtilityClass
public final class HomeAssistantRetrofitUtil {
	private HomeAssistantRetrofitUtil() { throw new UnsupportedOperationException(); }

	public static Retrofit.Builder defaultBuilder(Retrofit.Builder builder, String apiUrl){
		return builder.baseUrl(apiUrl)
				.addConverterFactory(JacksonConverterFactory.create(JacksonUtil.defaultMapper()))
				.addConverterFactory(ScalarsConverterFactory.create());
	}
}
