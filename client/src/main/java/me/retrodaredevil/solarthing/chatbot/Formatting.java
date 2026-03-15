package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import org.jspecify.annotations.NullMarked;

import java.text.DecimalFormat;
import java.text.Format;

@UtilityClass
@NullMarked
public final class Formatting {
	private Formatting() { throw new UnsupportedOperationException(); }

	public static final Format HUNDREDTHS_FORMAT = new DecimalFormat("0.00");
}
