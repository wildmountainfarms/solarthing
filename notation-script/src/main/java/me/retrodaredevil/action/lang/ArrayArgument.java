package me.retrodaredevil.action.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ArrayArgument implements Argument {
	private final List<Argument> values;

	public ArrayArgument(List<Argument> values) {
		this.values = Collections.unmodifiableList(new ArrayList<>(values));
	}

	public List<Argument> getValues() {
		return values;
	}
}
