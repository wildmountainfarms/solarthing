package me.retrodaredevil.solarthing.rest.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheDataPacket;
import me.retrodaredevil.solarthing.type.cache.packets.data.BatteryRecordDataCache;
import me.retrodaredevil.solarthing.type.cache.packets.data.ChargeControllerAccumulationDataCache;
import me.retrodaredevil.solarthing.type.cache.packets.data.FXAccumulationDataCache;
import me.retrodaredevil.solarthing.type.cache.packets.data.TemperatureRecordDataCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({ "/api/v1/cache" })
public class CacheController {

	private final CacheHandler cacheHandler;

	public CacheController(CacheHandler cacheHandler) {
		this.cacheHandler = cacheHandler;
	}


	@GetMapping(
			path = "/" + ChargeControllerAccumulationDataCache.CACHE_NAME,
			produces = "application/json"
	)
	public @NotNull List<@NotNull IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> getChargeControllerAccumulation(String sourceId, long startMillis, long endMillis) {
		final TypeReference<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> typeReference = new TypeReference<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>>() {};
		return cacheHandler.getCachesFromDateMillis(typeReference, ChargeControllerAccumulationDataCache.CACHE_NAME, sourceId, startMillis, endMillis);
	}
	@GetMapping(
			path = "/" + FXAccumulationDataCache.CACHE_NAME,
			produces = "application/json"
	)
	public @NotNull List<@NotNull IdentificationCacheDataPacket<FXAccumulationDataCache>> getFXAccumulation(String sourceId, long startMillis, long endMillis) {
		final TypeReference<IdentificationCacheDataPacket<FXAccumulationDataCache>> typeReference = new TypeReference<IdentificationCacheDataPacket<FXAccumulationDataCache>>() {};
		return cacheHandler.getCachesFromDateMillis(typeReference, FXAccumulationDataCache.CACHE_NAME, sourceId, startMillis, endMillis);
	}
	@GetMapping(
			path = "/" + BatteryRecordDataCache.CACHE_NAME,
			produces = "application/json"
	)
	public @NotNull List<@NotNull IdentificationCacheDataPacket<BatteryRecordDataCache>> getBatteryRecord(String sourceId, long startMillis, long endMillis) {
		final TypeReference<IdentificationCacheDataPacket<BatteryRecordDataCache>> typeReference = new TypeReference<>() {};
		return cacheHandler.getCachesFromDateMillis(typeReference, BatteryRecordDataCache.CACHE_NAME, sourceId, startMillis, endMillis);
	}

	@GetMapping(
			path = "/" + TemperatureRecordDataCache.CACHE_NAME,
			produces = "application/json"
	)
	public @NotNull List<@NotNull IdentificationCacheDataPacket<TemperatureRecordDataCache>> getTemperatureRecord(String sourceId, long startMillis, long endMillis) {
		final TypeReference<IdentificationCacheDataPacket<TemperatureRecordDataCache>> typeReference = new TypeReference<>() {};
		return cacheHandler.getCachesFromDateMillis(typeReference, TemperatureRecordDataCache.CACHE_NAME, sourceId, startMillis, endMillis);
	}
}
