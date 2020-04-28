package me.retrodaredevil.solarthing.pvoutput;

public class CsvUtil {
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
