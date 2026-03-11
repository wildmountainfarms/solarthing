package me.retrodaredevil.notation;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface NodeTranslator<T> {

	T translate(Node node);
}
