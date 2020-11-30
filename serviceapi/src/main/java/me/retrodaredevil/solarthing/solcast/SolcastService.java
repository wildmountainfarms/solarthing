package me.retrodaredevil.solarthing.solcast;

import me.retrodaredevil.solarthing.solcast.common.EstimatedActualResult;
import me.retrodaredevil.solarthing.solcast.common.ForecastResult;
import me.retrodaredevil.solarthing.solcast.rooftop.MeasurementData;
import me.retrodaredevil.solarthing.solcast.rooftop.MeasurementResult;
import retrofit2.Call;
import retrofit2.http.*;

public interface SolcastService {
	/**
	 * https://docs.solcast.com.au/#measurements-rooftop-site
	 * @param resourceId The resource ID
	 * @param measurementData The measurement data
	 * @return A Call where the resulting body is a the same as {@code measurementData} with the resource ID attached.
	 */
	@POST("rooftop_sites/{resource_id}/measurements.json")
	Call<MeasurementResult> postMeasurement(@Path("resource_id") String resourceId, @Body MeasurementData measurementData);


	@GET("rooftop_sites/{resource_id}/forecasts.json")
	Call<ForecastResult> retrieveForecasts(@Path("resource_id") String resourceId, @Query("hours") int hours);

	/**
	 * Same as {@link #retrieveForecasts(String, int)}, but defaults {@code hours} to 168 (1 week).
	 * @param resourceId The resource ID
	 * @return A Call where the resulting body is a ForecastResult
	 */
	@GET("rooftop_sites/{resource_id}/forecasts.json")
	Call<ForecastResult> retrieveForecasts(@Path("resource_id") String resourceId);


	/**
	 * Gets past estimated actual data (past forecasts)
	 * @param resourceId The resource ID
	 * @param hours The number of hours to get data back in the past.
	 * @return A Call where the resulting body is a EstimatedActualResult
	 */
	@GET("rooftop_sites/{resource_id}/estimated_actuals.json")
	Call<EstimatedActualResult> retrieveEstimatedActuals(@Path("resource_id") String resourceId, @Query("hours") int hours);

	/**
	 * Same as {@link #retrieveEstimatedActuals(String, int)}, but defaults {@code hours} to 168 (1 week).
	 * @param resourceId The resource ID
	 * @return A Call where the resulting body is a EstimatedActualResult
	 */
	@GET("rooftop_sites/{resource_id}/estimated_actuals.json")
	Call<EstimatedActualResult> retrieveEstimatedActuals(@Path("resource_id") String resourceId);
}
