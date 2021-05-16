package me.retrodaredevil.solarthing.rest.cache;

import me.retrodaredevil.solarthing.cache.CacheUtil;
import me.retrodaredevil.solarthing.cache.packets.IdentificationCacheDataPacket;
import me.retrodaredevil.solarthing.cache.packets.data.ChargeControllerAccumulationDataCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/cache")
public class CacheController {
	private static final Duration DURATION = Duration.ofMinutes(15);
	private static final int QUERY_PERIOD_COUNT = 4 * 24; // we can request data a day at a time, but we won't do more than that

	private final CacheHandler cacheHandler;

	public CacheController(CacheHandler cacheHandler) {
		this.cacheHandler = cacheHandler;
	}

	private static long getPeriodNumber(long dateMillis) {
		return dateMillis / DURATION.toMillis();
	}
	private static Instant getPeriodStartFromNumber(long periodNumber) {
		return Instant.ofEpochMilli(periodNumber * DURATION.toMillis());
	}
	private static Instant getPeriodStartFromMillis(long dateMillis) {
		return Instant.ofEpochMilli(getPeriodNumber(dateMillis) * DURATION.toMillis());
	}


	@GetMapping(
			path = "/" + ChargeControllerAccumulationDataCache.CACHE_NAME,
			produces = "application/json"
	)
	public List<IdentificationCacheDataPacket<ChargeControllerAccumulationDataCache>> getChargeControllerAccumulation(String sourceId, long startMillis, long endMillis) {
		final long endPeriodNumber = getPeriodNumber(endMillis);
		for (long periodNumber = getPeriodNumber(startMillis); periodNumber <= endPeriodNumber; periodNumber++) {
			Instant periodStart = getPeriodStartFromNumber(periodNumber);
			String documentId = CacheUtil.getDocumentId(periodStart, DURATION, sourceId, ChargeControllerAccumulationDataCache.CACHE_NAME);
		}


		throw new UnsupportedOperationException("TODO");
	}
}
