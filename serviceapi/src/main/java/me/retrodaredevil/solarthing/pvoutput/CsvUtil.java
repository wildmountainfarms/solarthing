package me.retrodaredevil.solarthing.pvoutput;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@UtilityClass
@NullMarked
public class CsvUtil {
	private CsvUtil() { throw new UnsupportedOperationException(); }
	public static String toCsvString(@Nullable String[] csv) {
		StringBuilder r = new StringBuilder();
		StringBuilder commas = new StringBuilder();

		for (String element : csv) {
			if (element != null) {
				r.append(commas).append(element);
				commas = new StringBuilder();
			}
			commas.append(',');
		}
		return r.toString();
	}
}
