package me.retrodaredevil.solarthing.solcast;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SolcastService {
	/**
	 * https://docs.solcast.com.au/#measurements-rooftop-site
	 * @param resourceId The resource ID
	 * @param measurementData The measurement data
	 * @return A Call where the resulting body is a String consisting of HTML that you can likely ignore
	 */
	@POST("rooftop_sites/{resource_id}/measurements")
	Call<String> postMeasurement(@Path("resource_id") String resourceId, @Body MeasurementData measurementData);
}
