package me.retrodaredevil.solarthing.commands.source;

public final class Sources {
	private Sources(){ throw new UnsupportedOperationException(); }
	
	public static Source createUnique(){
		return new Source() { };
	}
	public static Source createNamed(String name){
		return new NamedSource(name);
	}
}
