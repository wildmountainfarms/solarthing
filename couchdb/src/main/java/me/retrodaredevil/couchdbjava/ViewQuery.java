package me.retrodaredevil.couchdbjava;

import static java.util.Objects.requireNonNull;

public class ViewQuery {
	private final String designDoc;
	private final String viewName;
	private final ViewQueryParams params;

	public ViewQuery(String designDoc, String viewName, ViewQueryParams params) {
		requireNonNull(this.designDoc = designDoc);
		requireNonNull(this.viewName = viewName);
		requireNonNull(this.params = params);
	}

	public String getDesignDoc() {
		return designDoc;
	}

	public String getViewName() {
		return viewName;
	}

	public ViewQueryParams getParams() {
		return params;
	}

	@Override
	public String toString() {
		return "ViewQuery{" +
				"designDoc='" + designDoc + '\'' +
				", viewName='" + viewName + '\'' +
				", params=" + params +
				'}';
	}
}
