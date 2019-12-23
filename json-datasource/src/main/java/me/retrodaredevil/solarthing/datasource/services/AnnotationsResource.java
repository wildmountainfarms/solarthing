package me.retrodaredevil.solarthing.datasource.services;

import me.retrodaredevil.solarthing.datasource.endpoint.annotations.AnnotationRequest;
import me.retrodaredevil.solarthing.datasource.endpoint.annotations.AnnotationResult;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/annotations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnnotationsResource {
	@POST
	public List<AnnotationResult> get(AnnotationRequest request){
		System.out.println(request);
		return new ArrayList<>();
	}
}
