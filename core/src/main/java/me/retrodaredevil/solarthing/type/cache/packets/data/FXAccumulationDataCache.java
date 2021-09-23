package me.retrodaredevil.solarthing.type.cache.packets.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.accumulation.value.AccumulationValue;
import me.retrodaredevil.solarthing.solar.accumulation.value.AccumulationValueFactory;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXAccumulationData;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public class FXAccumulationDataCache extends BaseAccumulationDataCache implements IdentificationCacheData, OutbackData {
	private static final AccumulationValueFactory<Data> DATA_FACTORY = () -> Data.ZERO;
	public static final String CACHE_NAME = "fxAccumulation";

	private final OutbackIdentifier identifier;

	private final Data mainData;

	private final Data unknownData;

	@JsonCreator
	public FXAccumulationDataCache(
			@JsonProperty(value = "address", required = true) int address,
			@JsonProperty(value = "mainData", required = true) Data mainData,
			@JsonProperty(value = "firstDateMillis", required = true) Long firstDateMillis,
			@JsonProperty(value = "lastDateMillis", required = true) Long lastDateMillis,
			@JsonProperty(value = "unknownData", required = true) Data unknownData,
			@JsonProperty(value = "unknownStartDateMillis", required = true) Long unknownStartDateMillis) {
		super(firstDateMillis, lastDateMillis, unknownStartDateMillis);
		requireNonNull(this.mainData = mainData);
		requireNonNull(this.unknownData = unknownData);

		identifier = new OutbackIdentifier(address);
	}
	public static FXAccumulationDataCache createFromIdentifier(Identifier supplementaryIdentifier, Data mainData, Long firstDateMillis, Long lastDateMillis, Data unknownData, Long unknownStartDateMillis) {
		if (!(supplementaryIdentifier instanceof SupplementaryIdentifier)) {
			throw new IllegalArgumentException("The passed identifier is not a SupplementaryIdentifier! It is: " + supplementaryIdentifier.getClass() + " and its value is: " + supplementaryIdentifier.getRepresentation());
		}
		Identifier identifier = ((SupplementaryIdentifier) supplementaryIdentifier).getSupplementaryTo();
		if (!(identifier instanceof OutbackIdentifier)) {
			throw new IllegalArgumentException("identifier should be an OutbackIdentifier! It is: " + identifier.getClass() + " and its value is: " + identifier.getRepresentation());
		}
		OutbackIdentifier outbackIdentifier = (OutbackIdentifier) identifier;
		return new FXAccumulationDataCache(outbackIdentifier.getAddress(), mainData, firstDateMillis, lastDateMillis, unknownData, unknownStartDateMillis);
	}
	public static AccumulationValueFactory<Data> getDataFactory() {
		return DATA_FACTORY;
	}
	public static Data convert(DailyFXPacket packet) {
		return new Data(packet.getInverterKWH(), packet.getChargerKWH(), packet.getBuyKWH(), packet.getSellKWH());
	}

	@Override
	public FXAccumulationDataCache combine(IdentificationCacheData other) {
		FXAccumulationDataCache data = (FXAccumulationDataCache) other;
		if (isUnknown()) {
			return data;
		}
		if (data.isUnknown()) {
			return this;
		}
		return new FXAccumulationDataCache(
				identifier.getAddress(),
				mainData.plus(data.mainData).plus(data.unknownData),
				getFirstDateMillis(), data.getLastDateMillis(),
				unknownData,
				getUnknownStartDateMillis()
		);
	}

	@Override
	public @NotNull OutbackIdentifier getIdentifier() {
		return identifier;
	}

	@Override
	public int getAddress() { // JsonProperty added to superinterface
		return identifier.getAddress();
	}

	@JsonProperty("mainData")
	public @NotNull Data getMainData() {
		return mainData;
	}

	@JsonProperty("unknownData")
	public @NotNull Data getUnknownData() {
		return unknownData;
	}

	public static class Data implements FXAccumulationData, AccumulationValue<Data> {
		public static final Data ZERO = new Data(0f, 0f, 0f, 0f);

		private final float inverterKWH;
		private final float chargerKWH;
		private final float buyKWH;
		private final float sellKWH;

		@JsonCreator
		public Data(
				@JsonProperty(value = "inverterKWH", required = true) float inverterKWH,
				@JsonProperty(value = "chargerKWH", required = true) float chargerKWH,
				@JsonProperty(value = "buyKWH", required = true) float buyKWH,
				@JsonProperty(value = "sellKWH", required = true) float sellKWH) {
			this.inverterKWH = inverterKWH;
			this.chargerKWH = chargerKWH;
			this.buyKWH = buyKWH;
			this.sellKWH = sellKWH;
		}
		@Override
		public Data plus(Data other) {
			return new Data(
					inverterKWH + other.inverterKWH,
					chargerKWH + other.chargerKWH,
					buyKWH + other.buyKWH,
					sellKWH + other.sellKWH
			);
		}

		@Override
		public Data minus(Data other) {
			return new Data(
					inverterKWH - other.inverterKWH,
					chargerKWH - other.chargerKWH,
					buyKWH - other.buyKWH,
					sellKWH - other.sellKWH
			);
		}

		@Override
		public float getInverterKWH() {
			return inverterKWH;
		}

		@Override
		public float getChargerKWH() {
			return chargerKWH;
		}

		@Override
		public float getBuyKWH() {
			return buyKWH;
		}

		@Override
		public float getSellKWH() {
			return sellKWH;
		}
	}
}
