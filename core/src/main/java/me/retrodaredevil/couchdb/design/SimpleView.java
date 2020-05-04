package me.retrodaredevil.couchdb.design;

public class SimpleView implements View {
	private final String mapFunction;

	public SimpleView(String mapFunction) {
		this.mapFunction = mapFunction;
	}

	@Override
	public String getMapFunction() {
		return mapFunction;
	}
}
