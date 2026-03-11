package me.retrodaredevil.solarthing.rest.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheDataPacket;
import me.retrodaredevil.solarthing.type.cache.packets.data.BatteryRecordDataCache;
import me.retrodaredevil.solarthing.type.cache.packets.data.ChargeControllerAccumulationDataCache;
import me.retrodaredevil.solarthing.type.cache.packets.data.FXAccumulationDataCache;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({ "/cache", "/api/v1/cache" })
public class CacheController {

	private final CacheHandler cacheHandler;

	public CacheController(CacheHandler cacheHandler) {
		this.cacheHandler = cacheHandler;
	}


	@GetMapping(
			path = "/" + ChargeControllerAccumulationDataCache.CACHE_NAME,
			produces = "application/json"
	)
	public @NonNull List<@NonNull IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> getChargeControllerAccumulation(@NonNull String sourceId, long startMillis, long endMillis) {
		final TypeReference<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> typeReference = new TypeReference<>() {};
		return cacheHandler.getCachesFromDateMillis(typeReference, ChargeControllerAccumulationDataCache.CACHE_NAME, sourceId, startMillis, endMillis);
	}
	@GetMapping(
			path = "/" + FXAccumulationDataCache.CACHE_NAME,
			produces = "application/json"
	)
	public @NonNull List<@NonNull IdentificationCacheDataPacket<FXAccumulationDataCache>> getFXAccumulation(@NonNull String sourceId, long startMillis, long endMillis) {
		final TypeReference<IdentificationCacheDataPacket<FXAccumulationDataCache>> typeReference = new TypeReference<>() {};
		return cacheHandler.getCachesFromDateMillis(typeReference, FXAccumulationDataCache.CACHE_NAME, sourceId, startMillis, endMillis);
	}
	@GetMapping(
			path = "/" + BatteryRecordDataCache.CACHE_NAME,
			produces = "application/json"
	)
	public @NonNull List<@NonNull IdentificationCacheDataPacket<BatteryRecordDataCache>> getBatteryRecord(@NonNull String sourceId, long startMillis, long endMillis) {
		final TypeReference<IdentificationCacheDataPacket<BatteryRecordDataCache>> typeReference = new TypeReference<>() {};
		return cacheHandler.getCachesFromDateMillis(typeReference, BatteryRecordDataCache.CACHE_NAME, sourceId, startMillis, endMillis);
	}
}
