package me.retrodaredevil.notation;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NullMarked
public final class ArrayArgument implements Argument {
	private final List<Argument> values;

	public ArrayArgument(List<Argument> values) {
		this.values = Collections.unmodifiableList(new ArrayList<>(values));
	}

	public List<Argument> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return "[" + values.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
	}
}
