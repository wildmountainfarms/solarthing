package me.retrodaredevil.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class JsonHelper {
	private JsonHelper(){ throw new UnsupportedOperationException(); }
	
	public static <T> T getOrNull(JsonObject jsonObject, String memberName, Getter<T, JsonElement> getter){
		JsonElement element = jsonObject.get(memberName);
		if(element == null){
			return null;
		}
		return getter.get(element);
	}
	
	/**
	 * Should not be instantiated using an anonymous class, should use a lambda instead.
	 * @param <T> The return value type
	 * @param <H> The input value type
	 */
	public interface Getter<T, H>{
		T get(H h);
	}
}
