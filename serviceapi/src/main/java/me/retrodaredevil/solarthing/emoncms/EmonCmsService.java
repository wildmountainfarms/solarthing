package me.retrodaredevil.solarthing.emoncms;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EmonCmsService {

	@POST("input/post")
	Call<String> input(@Query("node") String node, @Query("fulljson") InputData data);
	@POST("input/post")
	Call<String> input(@Query("node") String node, @Query("fulljson") InputData data, @Query("time") long timeUnixSeconds);
}
