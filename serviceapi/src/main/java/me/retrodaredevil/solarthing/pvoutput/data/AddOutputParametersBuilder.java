package me.retrodaredevil.solarthing.pvoutput.data;

import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;
import me.retrodaredevil.solarthing.pvoutput.WeatherCondition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("unused")
@NullMarked
public class AddOutputParametersBuilder implements AddOutputParameters {
	private final SimpleDate date;
	private @Nullable Number generated;
	private @Nullable Number exported;
	private @Nullable Number peakPower;
	private @Nullable SimpleTime peakTime;
	private @Nullable String condition;
	private @Nullable Float minimumTemperatureCelsius;
	private @Nullable Float maximumTemperatureCelsius;
	private @Nullable String comments;
	private @Nullable Number importPeak;
	private @Nullable Number importOffPeak;
	private @Nullable Number importShoulder;
	private @Nullable Number importHighShoulder;
	private @Nullable Number consumption;
	private @Nullable Number exportPeak;
	private @Nullable Number exportOffPeak;
	private @Nullable Number exportShoulder;
	private @Nullable Number exportHighShoulder;

	public AddOutputParametersBuilder(SimpleDate date) {
		this.date = requireNonNull(date);
	}

	public AddOutputParameters build(){
		return new ImmutableAddOutputParameters(date, generated, exported, peakPower, peakTime, condition, minimumTemperatureCelsius, maximumTemperatureCelsius, comments, importPeak, importOffPeak, importShoulder, importHighShoulder, consumption, exportPeak, exportOffPeak, exportShoulder, exportHighShoulder);
	}

	@Override
	public SimpleDate getOutputDate() {
		return date;
	}

	public AddOutputParametersBuilder setGenerated(@Nullable Number generated) {
		this.generated = generated;
		return this;
	}

	@Override
	public @Nullable Number getGenerated() {
		return generated;
	}

	public AddOutputParametersBuilder setExported(@Nullable Number exported) {
		this.exported = exported;
		return this;
	}

	@Override
	public @Nullable Number getExported() {
		return exported;
	}

	public AddOutputParametersBuilder setPeakPower(@Nullable Number peakPower) {
		this.peakPower = peakPower;
		return this;
	}

	@Override
	public @Nullable Number getPeakPower() {
		return peakPower;
	}

	public AddOutputParametersBuilder setPeakTime(@Nullable SimpleTime peakTime) {
		this.peakTime = peakTime;
		return this;
	}

	@Override
	public @Nullable SimpleTime getPeakTime() {
		return peakTime;
	}

	public AddOutputParametersBuilder setConditionValue(@Nullable String condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public @Nullable String getConditionValue() {
		return condition;
	}

	public AddOutputParametersBuilder setCondition(WeatherCondition condition){
		this.condition = condition.toPVOutputString();
		return this;
	}

	public AddOutputParametersBuilder setMinimumTemperatureCelsius(@Nullable Float minimumTemperatureCelsius) {
		this.minimumTemperatureCelsius = minimumTemperatureCelsius;
		return this;
	}

	@Override
	public @Nullable Float getMinimumTemperatureCelsius() {
		return minimumTemperatureCelsius;
	}

	public AddOutputParametersBuilder setMaximumTemperatureCelsius(@Nullable Float maximumTemperatureCelsius) {
		this.maximumTemperatureCelsius = maximumTemperatureCelsius;
		return this;
	}

	@Override
	public @Nullable Float getMaximumTemperatureCelsius() {
		return maximumTemperatureCelsius;
	}

	/**
	 * @param comments The comments string. It is recommended to be less than 30 characters
	 */
	public AddOutputParametersBuilder setComments(@Nullable String comments) {
		this.comments = comments;
		return this;
	}

	@Override
	public @Nullable String getComments() {
		return comments;
	}

	public AddOutputParametersBuilder setImportPeak(@Nullable Number importPeak) {
		this.importPeak = importPeak;
		return this;
	}

	@Override
	public @Nullable Number getImportPeak() {
		return importPeak;
	}

	public AddOutputParametersBuilder setImportOffPeak(@Nullable Number importOffPeak) {
		this.importOffPeak = importOffPeak;
		return this;
	}

	@Override
	public @Nullable Number getImportOffPeak() {
		return importOffPeak;
	}

	public AddOutputParametersBuilder setImportShoulder(@Nullable Number importShoulder) {
		this.importShoulder = importShoulder;
		return this;
	}

	@Override
	public @Nullable Number getImportShoulder() {
		return importShoulder;
	}

	public AddOutputParametersBuilder setImportHighShoulder(@Nullable Number importHighShoulder) {
		this.importHighShoulder = importHighShoulder;
		return this;
	}

	@Override
	public @Nullable Number getImportHighShoulder() {
		return importHighShoulder;
	}

	public AddOutputParametersBuilder setConsumption(@Nullable Number consumption) {
		this.consumption = consumption;
		return this;
	}

	@Override
	public @Nullable Number getConsumption() {
		return consumption;
	}

	@Override public @Nullable Number getExportPeak() { return exportPeak; }
	public AddOutputParametersBuilder setExportPeak(@Nullable Number exportPeak) {
		this.exportPeak = exportPeak;
		return this;
	}

	@Override public @Nullable Number getExportOffPeak() { return exportOffPeak; }
	public AddOutputParametersBuilder setExportOffPeak(@Nullable Number exportOffPeak) {
		this.exportOffPeak = exportOffPeak;
		return this;
	}

	@Override public @Nullable Number getExportShoulder() { return exportShoulder; }
	public AddOutputParametersBuilder setExportShoulder(@Nullable Number exportShoulder) {
		this.exportShoulder = exportShoulder;
		return this;
	}

	@Override public @Nullable Number getExportHighShoulder() { return exportHighShoulder; }
	public AddOutputParametersBuilder setExportHighShoulder(@Nullable Number exportHighShoulder) {
		this.exportHighShoulder = exportHighShoulder;
		return this;
	}


	private static final class ImmutableAddOutputParameters implements AddOutputParameters {
		private final SimpleDate date;
		private final @Nullable Number generated;
		private final @Nullable Number exported;
		private final @Nullable Number peakPower;
		private final @Nullable SimpleTime peakTime;
		private final @Nullable String condition;
		private final @Nullable Float minimumTemperatureCelsius;
		private final @Nullable Float maximumTemperatureCelsius;
		private final @Nullable String comments;
		private final @Nullable Number importPeak;
		private final @Nullable Number importOffPeak;
		private final @Nullable Number importShoulder;
		private final @Nullable Number importHighShoulder;
		private final @Nullable Number consumption;
		private final @Nullable Number exportPeak, exportOffPeak, exportShoulder, exportHighShoulder;

		private ImmutableAddOutputParameters(SimpleDate date, @Nullable Number generated, @Nullable Number exported, @Nullable Number peakPower, @Nullable SimpleTime peakTime, @Nullable String condition, @Nullable Float minimumTemperatureCelsius, @Nullable Float maximumTemperatureCelsius, @Nullable String comments, @Nullable Number importPeak, @Nullable Number importOffPeak, @Nullable Number importShoulder, @Nullable Number importHighShoulder, @Nullable Number consumption, @Nullable Number exportPeak, @Nullable Number exportOffPeak, @Nullable Number exportShoulder, @Nullable Number exportHighShoulder) {
			this.date = requireNonNull(date);
			this.generated = generated;
			this.exported = exported;
			this.peakPower = peakPower;
			this.peakTime = peakTime;
			this.condition = condition;
			this.minimumTemperatureCelsius = minimumTemperatureCelsius;
			this.maximumTemperatureCelsius = maximumTemperatureCelsius;
			this.comments = comments;
			this.importPeak = importPeak;
			this.importOffPeak = importOffPeak;
			this.importShoulder = importShoulder;
			this.importHighShoulder = importHighShoulder;
			this.consumption = consumption;
			this.exportPeak = exportPeak;
			this.exportOffPeak = exportOffPeak;
			this.exportShoulder = exportShoulder;
			this.exportHighShoulder = exportHighShoulder;
		}

		@Override public SimpleDate getOutputDate() { return date; }
		@Override public @Nullable Number getGenerated() { return generated; }
		@Override public @Nullable Number getExported() { return exported; }
		@Override public @Nullable Number getPeakPower() { return peakPower; }
		@Override public @Nullable SimpleTime getPeakTime() { return peakTime; }
		@Override public @Nullable String getConditionValue() { return condition; }
		@Override public @Nullable Float getMinimumTemperatureCelsius() { return minimumTemperatureCelsius; }
		@Override public @Nullable Float getMaximumTemperatureCelsius() { return maximumTemperatureCelsius; }

		@Override public @Nullable String getComments() { return comments; }
		@Override public @Nullable Number getImportPeak() { return importPeak; }
		@Override public @Nullable Number getImportOffPeak() { return importOffPeak; }
		@Override public @Nullable Number getImportShoulder() { return importShoulder; }
		@Override public @Nullable Number getImportHighShoulder() { return importHighShoulder; }
		@Override public @Nullable Number getConsumption() { return consumption; }
		@Override public @Nullable Number getExportPeak() { return exportPeak; }
		@Override public @Nullable Number getExportOffPeak() { return exportOffPeak; }
		@Override public @Nullable Number getExportShoulder() { return exportShoulder; }
		@Override public @Nullable Number getExportHighShoulder() { return exportHighShoulder; }
	}
}
