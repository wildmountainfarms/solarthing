package me.retrodaredevil.solarthing.datasource.services;

import me.retrodaredevil.solarthing.datasource.endpoint.search.SearchRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SearchResource {
	@POST
	public List<Object> get(SearchRequest request){
		System.out.println(request);
		return new ArrayList<>();
	}
}
