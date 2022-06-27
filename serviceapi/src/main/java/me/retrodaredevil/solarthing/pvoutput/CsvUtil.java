package me.retrodaredevil.solarthing.pvoutput;

import me.retrodaredevil.solarthing.annotations.UtilityClass;

@UtilityClass
public class CsvUtil {
	private CsvUtil() { throw new UnsupportedOperationException(); }
	public static String toCsvString(String[] csv) {
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
