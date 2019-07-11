package me.retrodaredevil.solarthing.commands.source;

import java.util.Objects;

final class NamedSource implements Source{
	private final String name;
	
	public NamedSource(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "NamedSource{" + name + "}";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NamedSource that = (NamedSource) o;
		return Objects.equals(name, that.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
