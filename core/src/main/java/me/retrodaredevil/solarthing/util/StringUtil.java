package me.retrodaredevil.solarthing.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class StringUtil {
	private StringUtil() { throw new UnsupportedOperationException(); }

	public static String[] terminalSplit(String line) {
		String[] commentSplit = line.split("#");
		final String[] split;
		if(commentSplit.length == 0){
			split = new String[0];
		} else {
			List<String> splitList = new ArrayList<>(Arrays.asList(commentSplit[0].split(" ")));
			//noinspection StatementWithEmptyBody
			while(splitList.remove(""));

			split = splitList.toArray(new String[0]);
		}
		return split;
	}
}
