package me.retrodaredevil.solarthing.rest.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import me.retrodaredevil.solarthing.type.cache.packets.IdentificationCacheDataPacket;
import me.retrodaredevil.solarthing.type.cache.packets.data.ChargeControllerAccumulationDataCache;
import me.retrodaredevil.solarthing.type.cache.packets.data.FXAccumulationDataCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cache")
public class CacheController {

	private final CacheHandler cacheHandler;

	public CacheController(CacheHandler cacheHandler) {
		this.cacheHandler = cacheHandler;
	}


	@GetMapping(
			path = "/" + ChargeControllerAccumulationDataCache.CACHE_NAME,
			produces = "application/json"
	)
	public List<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> getChargeControllerAccumulation(String sourceId, long startMillis, long endMillis) {
		final TypeReference<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> typeReference = new TypeReference<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>>() {};
		return cacheHandler.getCachesFromDateMillis(typeReference, ChargeControllerAccumulationDataCache.CACHE_NAME, sourceId, startMillis, endMillis);
	}
	@GetMapping(
			path = "/" + FXAccumulationDataCache.CACHE_NAME,
			produces = "application/json"
	)
	public List<IdentificationCacheDataPacket<FXAccumulationDataCache>> getFXAccumulation(String sourceId, long startMillis, long endMillis) {
		final TypeReference<IdentificationCacheDataPacket<FXAccumulationDataCache>> typeReference = new TypeReference<IdentificationCacheDataPacket<FXAccumulationDataCache>>() {};
		return cacheHandler.getCachesFromDateMillis(typeReference, FXAccumulationDataCache.CACHE_NAME, sourceId, startMillis, endMillis);
	}
}
