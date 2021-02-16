package me.retrodaredevil.solarthing.pvoutput.service;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.serviceutil.HeaderRequestInterceptor;
import okhttp3.OkHttpClient;

@UtilityClass
public final class PVOutputOkHttpUtil {
	private PVOutputOkHttpUtil(){ throw new UnsupportedOperationException(); }

	public static OkHttpClient.Builder configure(OkHttpClient.Builder builder, String apiKey, long systemId){
		return builder
				.addInterceptor(new HeaderRequestInterceptor("X-Pvoutput-Apikey", apiKey))
				.addInterceptor(new HeaderRequestInterceptor("X-Pvoutput-SystemId", "" + systemId));
	}
}
