package me.retrodaredevil.solarthing.datasource.services;

import me.retrodaredevil.grafana.datasource.endpoint.DataPoint;
import me.retrodaredevil.grafana.datasource.endpoint.Target;
import me.retrodaredevil.grafana.datasource.endpoint.annotations.AnnotationRequest;
import me.retrodaredevil.grafana.datasource.endpoint.annotations.AnnotationResult;
import me.retrodaredevil.grafana.datasource.endpoint.annotations.AnnotationsResource;
import me.retrodaredevil.grafana.datasource.endpoint.query.QueryRequest;
import me.retrodaredevil.grafana.datasource.endpoint.query.QueryResource;
import me.retrodaredevil.grafana.datasource.endpoint.query.QueryResult;
import me.retrodaredevil.grafana.datasource.endpoint.query.TimeSeriesResult;
import me.retrodaredevil.grafana.datasource.endpoint.root.RootResource;
import me.retrodaredevil.grafana.datasource.endpoint.search.SearchRequest;
import me.retrodaredevil.grafana.datasource.endpoint.search.SearchResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolarMasterResource implements RootResource, SearchResource, QueryResource, AnnotationsResource {
	@Path("/") @GET
	@Override
	public Response checkConnection(){
		return Response.ok().build();
	}
	@Path("/search") @POST
	@Override
	public List<Object> getSuggestions(SearchRequest request){
		System.out.println(request);
		return new ArrayList<>();
	}
	@Path("/query") @POST
	@Override
	public List<QueryResult> queryMetrics(QueryRequest request){
		System.out.println(request);
		List<QueryResult> r = new ArrayList<>();
		for(Target targetObject : request.getTargets()){
			String target = targetObject.getTarget();
			r.add(new TimeSeriesResult(target, Arrays.asList(
					new DataPoint(10, System.currentTimeMillis() - 1000),
					new DataPoint(4, System.currentTimeMillis() - 1000 * 60),
					new DataPoint(20, System.currentTimeMillis() - 1000 * 60 * 60)
			)));
		}
		return r;
	}
	@Path("/annotations") @POST
	@Override
	public List<AnnotationResult> queryAnnotations(AnnotationRequest request) {
		System.out.println(request);
		return new ArrayList<>();
	}
}
