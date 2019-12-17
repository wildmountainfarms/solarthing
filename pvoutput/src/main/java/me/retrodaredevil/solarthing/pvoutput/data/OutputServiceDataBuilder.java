package me.retrodaredevil.solarthing.pvoutput.data;

import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("unused")
public class OutputServiceDataBuilder {
	private final SimpleDate date;
	private Number generated;
	private Number exported;
	private Number peakPower;
	private SimpleTime peakTime;
	private String condition;
	private Float minimumTemperatureCelsius;
	private Float maximumTemperatureCelsius;
	private String comments;
	private Number importPeak;
	private Number importOffPeak;
	private Number importShoulder;
	private Number importHighShoulder;
	private Number consumption;

	public OutputServiceDataBuilder(SimpleDate date) {
		this.date = requireNonNull(date);
	}

	public OutputServiceData build(){
		return new ImmutableOutputServiceData(date, generated, exported, peakPower, peakTime, condition, minimumTemperatureCelsius, maximumTemperatureCelsius, comments, importPeak, importOffPeak, importShoulder, importHighShoulder, consumption);
	}

	public OutputServiceDataBuilder setGenerated(Number generated) {
		this.generated = generated;
		return this;
	}

	public OutputServiceDataBuilder setExported(Number exported) {
		this.exported = exported;
		return this;
	}

	public OutputServiceDataBuilder setPeakPower(Number peakPower) {
		this.peakPower = peakPower;
		return this;
	}

	public OutputServiceDataBuilder setPeakTime(SimpleTime peakTime) {
		this.peakTime = peakTime;
		return this;
	}

	public OutputServiceDataBuilder setCondition(String condition) {
		this.condition = condition;
		return this;
	}
	public OutputServiceDataBuilder setCondition(WeatherCondition condition){
		this.condition = condition.toPVOutputString();
		return this;
	}

	public OutputServiceDataBuilder setMinimumTemperatureCelsius(Float minimumTemperatureCelsius) {
		this.minimumTemperatureCelsius = minimumTemperatureCelsius;
		return this;
	}

	public OutputServiceDataBuilder setMaximumTemperatureCelsius(Float maximumTemperatureCelsius) {
		this.maximumTemperatureCelsius = maximumTemperatureCelsius;
		return this;
	}

	public OutputServiceDataBuilder setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public OutputServiceDataBuilder setImportPeak(Number importPeak) {
		this.importPeak = importPeak;
		return this;
	}

	public OutputServiceDataBuilder setImportOffPeak(Number importOffPeak) {
		this.importOffPeak = importOffPeak;
		return this;
	}

	public OutputServiceDataBuilder setImportShoulder(Number importShoulder) {
		this.importShoulder = importShoulder;
		return this;
	}

	public OutputServiceDataBuilder setImportHighShoulder(Number importHighShoulder) {
		this.importHighShoulder = importHighShoulder;
		return this;
	}

	public OutputServiceDataBuilder setConsumption(Number consumption) {
		this.consumption = consumption;
		return this;
	}


	private static final class ImmutableOutputServiceData implements OutputServiceData {
		private final SimpleDate date;
		private final Number generated;
		private final Number exported;
		private final Number peakPower;
		private final SimpleTime peakTime;
		private final String condition;
		private final Float minimumTemperatureCelsius;
		private final Float maximumTemperatureCelsius;
		private final String comments;
		private final Number importPeak;
		private final Number importOffPeak;
		private final Number importShoulder;
		private final Number importHighShoulder;
		private final Number consumption;

		private ImmutableOutputServiceData(SimpleDate date, Number generated, Number exported, Number peakPower, SimpleTime peakTime, String condition, Float minimumTemperatureCelsius, Float maximumTemperatureCelsius, String comments, Number importPeak, Number importOffPeak, Number importShoulder, Number importHighShoulder, Number consumption) {
			this.date = date;
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
		}

		@Override public SimpleDate getOutputDate() { return date; }
		@Override public Number getGenerated() { return generated; }
		@Override public Number getExported() { return exported; }
		@Override public Number getPeakPower() { return peakPower; }
		@Override public SimpleTime getPeakTime() { return peakTime; }
		@Override public String getConditionValue() { return condition; }
		@Override public Float getMinimumTemperatureCelsius() { return minimumTemperatureCelsius; }
		@Override public Float getMaximumTemperatureCelsius() { return maximumTemperatureCelsius; }

		@Override public String getComments() { return comments; }
		@Override public Number getImportPeak() { return importPeak; }
		@Override public Number getImportOffPeak() { return importOffPeak; }
		@Override public Number getImportShoulder() { return importShoulder; }
		@Override public Number getImportHighShoulder() { return importHighShoulder; }
		@Override public Number getConsumption() { return consumption; }
	}
}
