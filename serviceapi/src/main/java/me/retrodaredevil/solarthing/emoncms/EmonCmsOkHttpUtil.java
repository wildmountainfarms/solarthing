package me.retrodaredevil.solarthing.emoncms;

import org.jspecify.annotations.NullMarked;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.serviceutil.HeaderRequestInterceptor;
import okhttp3.OkHttpClient;

@UtilityClass

@NullMarked
public final class EmonCmsOkHttpUtil {
	private EmonCmsOkHttpUtil(){ throw new UnsupportedOperationException(); }

	public static OkHttpClient.Builder configure(OkHttpClient.Builder builder, String apiKey){
		return builder
				.addInterceptor(new HeaderRequestInterceptor("Authorization", "Bearer " + apiKey));
	}
}
