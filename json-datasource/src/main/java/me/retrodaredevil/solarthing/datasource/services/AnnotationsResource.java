package me.retrodaredevil.solarthing.datasource.services;

import me.retrodaredevil.solarthing.datasource.endpoint.annotations.AnnotationRequest;
import me.retrodaredevil.solarthing.datasource.endpoint.annotations.AnnotationResult;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/annotations")
public class AnnotationsResource {
	@GET
	public List<AnnotationResult> get(AnnotationRequest request){
		throw new UnsupportedOperationException("TODO");
	}
}
