package me.retrodaredevil.solarthing.packets.collection;

import java.util.Comparator;

public final class FragmentUtil {
	private FragmentUtil() { throw new UnsupportedOperationException(); }

	public static final Comparator<Integer> DEFAULT_FRAGMENT_ID_COMPARATOR = Integer::compare;

	public static Comparator<Integer> createPriorityComparator(int prioritizedFragmentId) {
		return (fragment1, fragment2) -> {
			if (fragment1 == prioritizedFragmentId) { // fragment1 is less than fragment2
				return -1;
			}
			if (fragment2 == prioritizedFragmentId) { // fragment1 is greater than fragment2
				return 1;
			}
			return fragment1 - fragment2;
		};
	}
}
