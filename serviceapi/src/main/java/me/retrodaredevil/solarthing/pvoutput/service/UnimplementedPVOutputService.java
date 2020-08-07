package me.retrodaredevil.solarthing.pvoutput.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

interface UnimplementedPVOutputService {

//	@POST("ravenpost.jsp") void ravenPost();
//	@POST("eaglepost.jsp") void eaglePost();
	// Implement Billion Gateway
	// Implement eGauge Post

	@POST("addbatchstatus.jsp") // or get
	Call<?> addBatchStatus();


	@GET("getstatus.jsp")
	Object getStatus();
	@GET("getstatistic.jsp")
	Object getStatistic();
	@GET("getsystem.jsp")
	Object getSystem();
	@POST("postsystem.jsp")
	void postSystem();
	@GET("getoutput.jsp")
	Object getOutput();
	@GET("getextended.jsp")
	Object getExtended();
	@GET("getfavourite.jsp") // yes, we're going to use favourite to stay consistent
	Object getFavourite();
	@GET("getmissing.jsp")
	Object getMissing();
	/** https://pvoutput.org/help.html#api-getinsolation */
	@GET("getinsolation.jsp")
	Object getInsolation();
	@POST("deletestatus.jsp") // or get
	void deleteStatus();
	@POST("search.jsp") // or get
	Object search();

	// region Team
	@GET("getteam.jsp")
	Object getTeam();
	@GET("leaveteam.jsp")
	void leaveTeam();
	// endregion
	@GET("getsupply.jsp")
	Object getSupply();

	@GET("registernotification.jsp")
	void registerNotification();
	@GET("deregisternotification.jsp")
	void deregisterNotification();

}
