package me.retrodaredevil.solarthing.solcast;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SolcastService {
	@POST("rooftop_sites/{resource_id}/measurements")
	Call<String> postMeasurement(@Path("resource_id") String resourceId, @Body MeasurementData measurementData);
}
