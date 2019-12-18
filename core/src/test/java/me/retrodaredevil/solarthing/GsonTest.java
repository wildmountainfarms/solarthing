package me.retrodaredevil.solarthing;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GsonTest {
	@Test
	void test(){
		String json = new GsonBuilder().create().toJson(new TestClass());
		System.out.println(json);
	}
	private static class TestClass {
		private final int[] hi = {1, 2, 4, 6};
		private final List<Integer> intList = Arrays.asList(2, 5, 9);
		private final Set<Integer> intSet = new HashSet<>(Arrays.asList(2, 5, 9));
	}
}
