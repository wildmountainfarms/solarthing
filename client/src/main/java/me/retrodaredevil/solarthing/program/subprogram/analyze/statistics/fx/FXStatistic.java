package me.retrodaredevil.solarthing.program.subprogram.analyze.statistics.fx;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a collection of <em>some statistic</em> over some period of time for a single FX.
 * {@link me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket}
 *
 * Depending on what was happening during the time period this represents (AC Use, AC Drop, Silent, Bulk, the entire generator run time)
 * or the type of statistic this is, some properties may not be useful.
 * Usually many of these properties become more useful for the shorter periods of time that are focused on.
 *
 */
public record FXStatistic<T>(
		T batteryVoltage,

		T inverterCurrentRaw,
		T inverterWattage,

		T chargerCurrentRaw,
		T chargerWattage,

		T buyCurrentRaw,
		T buyWattage,

		T sellCurrentRaw,
		T sellWattage,

		T inputVoltageRaw,
		T outputVoltageRaw
) {

	public static <T> FXStatistic<T> initUsing(Supplier<T> initializer) {
		return new FXStatistic<>(
				initializer.get(),

				initializer.get(),
				initializer.get(),

				initializer.get(),
				initializer.get(),

				initializer.get(),
				initializer.get(),

				initializer.get(),
				initializer.get(),

				initializer.get(),
				initializer.get()
		);
	}

	public <U> FXStatistic<U> convert(Function<T, U> converter) {
		return new FXStatistic<>(
				converter.apply(batteryVoltage),

				converter.apply(inverterCurrentRaw),
				converter.apply(inverterWattage),

				converter.apply(chargerCurrentRaw),
				converter.apply(chargerWattage),

				converter.apply(buyCurrentRaw),
				converter.apply(buyWattage),

				converter.apply(sellCurrentRaw),
				converter.apply(sellWattage),

				converter.apply(inputVoltageRaw),
				converter.apply(outputVoltageRaw)
		);
	}
	public <U> void apply(FXStatistic<U> other, BiConsumer<T, U> function) {
		function.accept(batteryVoltage, other.batteryVoltage);

		function.accept(inverterCurrentRaw, other.inverterCurrentRaw);
		function.accept(inverterWattage, other.inverterWattage);

		function.accept(chargerCurrentRaw, other.chargerCurrentRaw);
		function.accept(chargerWattage, other.chargerWattage);

		function.accept(buyCurrentRaw, other.buyCurrentRaw);
		function.accept(buyWattage, other.buyWattage);

		function.accept(sellCurrentRaw, other.sellCurrentRaw);
		function.accept(sellWattage, other.sellWattage);

		function.accept(inputVoltageRaw, other.inputVoltageRaw);
		function.accept(outputVoltageRaw, other.outputVoltageRaw);
	}
}
