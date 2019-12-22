package me.retrodaredevil.solarthing.datasource.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/root")
public class RootResource {
	@GET
	public Response get(){
		return Response.ok().build();
	}
}
