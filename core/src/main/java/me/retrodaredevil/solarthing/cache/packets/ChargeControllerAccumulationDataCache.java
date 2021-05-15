package me.retrodaredevil.solarthing.cache.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.cache.IdentificationCacheData;
import me.retrodaredevil.solarthing.packets.identification.Identifier;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public class ChargeControllerAccumulationDataCache extends BaseAccumulationDataCache implements IdentificationCacheData {
	/*
	generationKWH represents the main generation that took place during a certain period
	firstDateMillis is normally the last packet from the period before this one. However, if the last period
	has no packets, firstDateMillis will be the first packet's date from this period and if possible, unknownGenerationStartDateMillis
	will be the latest packet before the current period

	In the future, we may support generated AH, but I don't care about that as much, so it's not implemented right now mostly because not all charge controllers support it
	 */

	private final Identifier identifier;

	private final float generationKWH;

	private final float unknownGenerationKWH;

	@JsonCreator
	public ChargeControllerAccumulationDataCache(
			@JsonProperty(value = "identifier", required = true) Identifier identifier,
			@JsonProperty(value = "generationKWH", required = true) float generationKWH,
			@JsonProperty(value = "firstDateMillis", required = true) Long firstDateMillis,
			@JsonProperty(value = "lastDateMillis", required = true) Long lastDateMillis,
			@JsonProperty(value = "unknownGenerationKWH", required = true) float unknownGenerationKWH,
			@JsonProperty(value = "unknownStartDateMillis", required = true) Long unknownStartDateMillis) {
		super(firstDateMillis, lastDateMillis, unknownStartDateMillis);
		requireNonNull(this.identifier = identifier);
		this.generationKWH = generationKWH;
		this.unknownGenerationKWH = unknownGenerationKWH;
	}

	@Override
	public ChargeControllerAccumulationDataCache combine(IdentificationCacheData other) {
		ChargeControllerAccumulationDataCache data = (ChargeControllerAccumulationDataCache) other;
		if (isUnknown()) {
			return data;
		}
		if (data.isUnknown()) {
			return this;
		}
		return new ChargeControllerAccumulationDataCache(
				identifier,
				generationKWH + data.generationKWH + data.unknownGenerationKWH,
				getFirstDateMillis(), data.getLastDateMillis(),
				unknownGenerationKWH,
				getUnknownStartDateMillis()
		);
	}

	@JsonProperty("identifier")
	@Override
	public @NotNull Identifier getIdentifier() {
		return identifier;
	}

	@JsonProperty("generationKWH")
	public float getGenerationKWH() {
		return generationKWH;
	}

	@JsonProperty("unknownGenerationKWH")
	public float getUnknownGenerationKWH() {
		return unknownGenerationKWH;
	}

}
