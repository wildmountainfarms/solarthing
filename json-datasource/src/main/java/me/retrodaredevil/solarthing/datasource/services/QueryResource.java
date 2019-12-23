package me.retrodaredevil.solarthing.datasource.services;

import me.retrodaredevil.solarthing.datasource.endpoint.DataPoint;
import me.retrodaredevil.solarthing.datasource.endpoint.Target;
import me.retrodaredevil.solarthing.datasource.endpoint.query.QueryRequest;
import me.retrodaredevil.solarthing.datasource.endpoint.query.QueryResult;
import me.retrodaredevil.solarthing.datasource.endpoint.query.TimeSeriesResult;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/query")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QueryResource {
	@POST
	public List<QueryResult> get(QueryRequest request){
		System.out.println(request);
		List<QueryResult> r = new ArrayList<>();
		for(Target target : request.getTargets()){
			r.add(new TimeSeriesResult(target.getTarget(), Arrays.asList(
					new DataPoint(10, System.currentTimeMillis() - 1000),
					new DataPoint(4, System.currentTimeMillis() - 1000 * 60),
					new DataPoint(20, System.currentTimeMillis() - 1000 * 60 * 60)
			)));
		}
		return r;
	}
}
