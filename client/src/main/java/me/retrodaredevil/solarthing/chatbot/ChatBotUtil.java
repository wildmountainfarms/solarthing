package me.retrodaredevil.solarthing.chatbot;

import info.debatty.java.stringsimilarity.JaroWinkler;
import me.retrodaredevil.solarthing.annotations.UtilityClass;

import java.util.function.Function;

@UtilityClass
public final class ChatBotUtil {
	private ChatBotUtil() { throw new UnsupportedOperationException(); }
	private static final JaroWinkler MATCHER = new JaroWinkler();
	private static final double SIMILARITY_CONSTANT = 0.86;

	public static double similarity(String s1, String s2) {
		return MATCHER.similarity(s1.toLowerCase(), s2.toLowerCase());
	}
	public static boolean isSimilar(String s1, String s2) {
		return similarity(s1, s2) > SIMILARITY_CONSTANT;
	}
	public static <T> T findBest(Iterable<? extends T> iterable, Function<? super T, ? extends String> toText, String text) {
		T best = null;
		double bestSimilarity = 0;
		for (T element : iterable) {
			double similarity = ChatBotUtil.similarity(text, toText.apply(element));
			if (similarity > SIMILARITY_CONSTANT && similarity > bestSimilarity) {
				best = element;
				bestSimilarity = similarity;
			}
		}
		return best;
	}
}
