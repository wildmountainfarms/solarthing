package me.retrodaredevil.solarthing.pvoutput.service;

import me.retrodaredevil.solarthing.pvoutput.data.AddBatchOutputParameters;
import me.retrodaredevil.solarthing.pvoutput.data.AddOutputParameters;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParameters;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PVOutputService {
	// Start Service API // https://pvoutput.org/help.html#api-spec
	@POST("addoutput.jsp")
	Call<String> addOutput(@Body AddOutputParameters parameters);
	@POST("addstatus.jsp") // or get
	Call<String> addStatus(@Body AddStatusParameters parameters);
	@POST("addbatchoutput.jsp")
	Call<String> addBatchOutput(@Body AddBatchOutputParameters parameters);

	@GET("jointeam.jsp")
	Call<String> joinTeam(@Query("tid") int teamId);
}
