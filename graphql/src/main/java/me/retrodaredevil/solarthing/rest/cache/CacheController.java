package me.retrodaredevil.solarthing.rest.cache;

import me.retrodaredevil.solarthing.cache.packets.IdentificationCacheDataPacket;
import me.retrodaredevil.solarthing.cache.packets.data.ChargeControllerAccumulationDataCache;
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
		return cacheHandler.getChargeControllerAccumulation(sourceId, cacheHandler.getPeriodNumber(startMillis), cacheHandler.getPeriodNumber(endMillis));
	}
}
