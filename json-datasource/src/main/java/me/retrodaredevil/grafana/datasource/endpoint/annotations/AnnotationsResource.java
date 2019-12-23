package me.retrodaredevil.grafana.datasource.endpoint.annotations;

import java.util.List;

public interface AnnotationsResource {
	List<AnnotationResult> queryAnnotations(AnnotationRequest request);
}
