package me.retrodaredevil.solarthing.util.frequency;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrequentHandler<T> {
	private final List<? extends FrequentObject<T>> frequentObjectList;

	private final Map<FrequentObject<T>, Integer> frequentMap = new HashMap<>();

	/**
	 * @param frequentObjectList The list of {@link FrequentObject}s that will be used. Mutations to this object will have an affect.
	 */
	public FrequentHandler(List<? extends FrequentObject<T>> frequentObjectList) {
		this.frequentObjectList = frequentObjectList;
	}

	public FrequentObject<T> get(double progress){
		if(progress < 0 || progress >= 1) throw new IllegalArgumentException("progress must be between 0 and 1. range: [0..1)");
		FrequentObject<T> defaultObject = null;
		for(FrequentObject<T> object : frequentObjectList){
			Integer frequency = object.getFrequency();
			if(frequency == null){
				if(defaultObject != null){
					throw new IllegalStateException("More than one of the FrequentObjects in the list had null frequencies! Only one is allowed to be null (default)!");
				}
				defaultObject = object;
				continue;
			}
			int amount = frequentMap.getOrDefault(object, 0);
			double objectProgress = ((double)amount) / frequency;
			if(objectProgress < progress){
				return object;
			}
		}
		return defaultObject;
	}
	public void use(FrequentObject<T> object){
		int current = frequentMap.getOrDefault(object, 0);
		frequentMap.put(object, current + 1);
	}
	public void reset(){
		frequentMap.clear();
	}
}
