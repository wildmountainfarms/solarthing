package me.retrodaredevil.solarthing.datasource.endpoint.search;

import java.beans.ConstructorProperties;

public class SearchRequest {
	private final String target;
	private final String type;

	@ConstructorProperties({ "target", "type" })
	public SearchRequest(String target, String type) {
		if(target == null) throw new NullPointerException("target is null!");
		this.target = target;
		this.type = type;
	}

	public String getTarget() {
		return target;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		String type = this.type == null ? "null" : "'" + this.type + "'";
		return "SearchRequest(" +
				"target='" + target + '\'' +
				", type=" + type +
				')';
	}
}
