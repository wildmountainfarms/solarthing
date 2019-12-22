package me.retrodaredevil.solarthing.datasource.services;

import me.retrodaredevil.solarthing.datasource.endpoint.query.QueryRequest;
import me.retrodaredevil.solarthing.datasource.endpoint.query.QueryResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/query")
@Produces(MediaType.APPLICATION_JSON)
public class QueryResource {
	@GET
	public List<QueryResponse> get(QueryRequest request){
		return new ArrayList<>();
	}
}
