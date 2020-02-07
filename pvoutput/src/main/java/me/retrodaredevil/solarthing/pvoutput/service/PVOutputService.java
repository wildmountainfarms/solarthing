package me.retrodaredevil.solarthing.pvoutput.service;

import me.retrodaredevil.solarthing.pvoutput.data.AddOutputParamters;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParameters;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PVOutputService {
	// Start Service API // https://pvoutput.org/help.html#api-spec
	@POST("addoutput.jsp")
	Call<String> addOutput(@Body AddOutputParamters parameters);
	@POST("addstatus.jsp") // or get
	Call<String> addStatus(@Body AddStatusParameters parameters);

}
