package me.retrodaredevil.solarthing.homeassistant;

import me.retrodaredevil.solarthing.homeassistant.data.BinarySensorState;
import me.retrodaredevil.solarthing.homeassistant.data.SensorInfo;
import me.retrodaredevil.solarthing.homeassistant.data.SensorState;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface HomeAssistantService {
	@POST("states/sensor.{name}")
	Call<SensorInfo> updateSensor(@Path("name") String name, @Body SensorState data);
	@POST("states/binary_sensor.{name}")
	Call<SensorInfo> updateBinarySensor(@Path("name") String name, @Body BinarySensorState data);
}
