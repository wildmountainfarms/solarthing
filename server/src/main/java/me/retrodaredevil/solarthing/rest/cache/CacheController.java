package me.retrodaredevil.solarthing.rest.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheDataPacket;
import me.retrodaredevil.solarthing.type.cache.packets.data.BatteryRecordDataCache;
import me.retrodaredevil.solarthing.type.cache.packets.data.ChargeControllerAccumulationDataCache;
import me.retrodaredevil.solarthing.type.cache.packets.data.FXAccumulationDataCache;
import org.jspecify.annotations.NullMarked;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping({ "/cache", "/api/v1/cache" })
@NullMarked
public class CacheController {

	private final CacheHandler cacheHandler;

	public CacheController(CacheHandler cacheHandler) {
		this.cacheHandler = cacheHandler;
	}


	@GetMapping(
			path = "/" + ChargeControllerAccumulationDataCache.CACHE_NAME,
			produces = "application/json"
	)
	public List<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> getChargeControllerAccumulation(@RequestParam String sourceId, @RequestParam long startMillis, @RequestParam long endMillis) {
		requireNonNull(sourceId, "sourceId");
		final TypeReference<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> typeReference = new TypeReference<>() {};
		return cacheHandler.getCachesFromDateMillis(typeReference, ChargeControllerAccumulationDataCache.CACHE_NAME, sourceId, startMillis, endMillis);
	}
	@GetMapping(
			path = "/" + FXAccumulationDataCache.CACHE_NAME,
			produces = "application/json"
	)
	public List<IdentificationCacheDataPacket<FXAccumulationDataCache>> getFXAccumulation(@RequestParam String sourceId, @RequestParam long startMillis, @RequestParam long endMillis) {
		requireNonNull(sourceId, "sourceId");
		final TypeReference<IdentificationCacheDataPacket<FXAccumulationDataCache>> typeReference = new TypeReference<>() {};
		return cacheHandler.getCachesFromDateMillis(typeReference, FXAccumulationDataCache.CACHE_NAME, sourceId, startMillis, endMillis);
	}
	@GetMapping(
			path = "/" + BatteryRecordDataCache.CACHE_NAME,
			produces = "application/json"
	)
	public List<IdentificationCacheDataPacket<BatteryRecordDataCache>> getBatteryRecord(@RequestParam String sourceId, @RequestParam long startMillis, @RequestParam long endMillis) {
		requireNonNull(sourceId, "sourceId");
		final TypeReference<IdentificationCacheDataPacket<BatteryRecordDataCache>> typeReference = new TypeReference<>() {};
		return cacheHandler.getCachesFromDateMillis(typeReference, BatteryRecordDataCache.CACHE_NAME, sourceId, startMillis, endMillis);
	}
}
